/*  ==== Agent Widget  v1.3 (backend TTS/STT, auto-listen) ====  */
(function () {
  /* ===== 1. CONFIG ===== */
  const cfg = window.AgentBuilder || {
    botId: "bot",
    apiUrl: "http://localhost:8081/api/rag/chat",
    themeColor: "#4CAF50",
    textColor: "#ffffff",
    textFont: "Inter",
    welcomeMessage: "How can I help?",
    botAvatar: "",
    // NEW: backend endpoints for audio
    ttsUrl: "/api/audio/tts",          // GET  text=<msg>
    sttUrl: "/api/audio/stt",            // POST multipart file,
    voiceId: "T5cu6IU92Krx4mh43osx"
  };

  /* ===== 2. CSS (unchanged except fonts) ===== */
  const css = `@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap');
  body{font-family:'Inter',sans-serif}
  #agent_launcher{position:fixed;bottom:20px;right:20px;background:linear-gradient(135deg,#0078ff,#00c6ff);color:#fff;font-size:22px;padding:10px;border-radius:50%;cursor:pointer;z-index:9998;transition:transform .3s}
  #agent_launcher:hover{transform:scale(1.1)}
  #agent_widget{position:fixed;bottom:90px;right:20px;width:340px;background:#fff;border:1px solid #ddd;border-radius:12px;box-shadow:0 8px 25px rgba(0,0,0,.15);z-index:9999;display:flex;flex-direction:column;overflow:hidden;animation:fadeInUp .3s}
  @keyframes fadeInUp{from{opacity:0;transform:translateY(30px)}to{opacity:1;transform:translateY(0)}}
  .agent-header{padding:12px 16px;font-weight:600;display:flex;justify-content:space-between;align-items:center;font-size:15px;border-bottom:1px solid #ddd}
  .agent-close{cursor:pointer;font-size:20px;transition:color .2s}
  .agent-close:hover{color:#000}
  .agent-body{display:flex;flex-direction:column;height:100%}
  .message-container{height:350px;overflow-y:auto;background:#fafafa;padding:12px;display:flex;flex-direction:column;gap:10px;scrollbar-width:thin}
  .message{display:flex;align-items:flex-start;gap:8px;margin-top:6px;font-size:14px;max-width:80%;word-wrap:break-word}
  .message.user{align-self:flex-end;flex-direction:row-reverse}
  .message.user .message-content{background:#efefef;color:#000;padding:8px;border-radius:8px}
  .message.bot{align-self:flex-start}
  .message.bot .message-content{padding:8px;border-radius:8px}
  .bot-avatar{width:24px;height:24px;border-radius:50%}
  .input-area{display:flex;padding:4px;background:#fff;border-top:1px solid #ddd;gap:8px}
  .input-area input{flex:1;padding:10px 12px;border-radius:8px;border:none;font-size:14px;outline:none}
  .input-area button{padding:8px 12px;background:transparent;border:none;border-radius:8px;cursor:pointer;font-size:18px}`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  /* ===== 3. DOM ===== */
  const launcher = Object.assign(document.createElement("div"), {
    id: "agent_launcher",
    textContent: "💬",
    style: `background:${cfg.themeColor};color:${cfg.textColor}`
  });
  document.body.appendChild(launcher);

  const widget = document.createElement("div");
  widget.id = "agent_widget";
  widget.style.display = "none";
  widget.innerHTML = `
    <div class="agent-header" style="background:${cfg.themeColor};color:${cfg.textColor}">
      ${cfg.botName ?? "Agent"} – ${cfg.botId}
      <span class="agent-close" style="color:${cfg.textColor}">&times;</span>
    </div>
    <div class="agent-body">
      <div class="message-container" id="message_list">
        <div class="message bot">
          ${cfg.botAvatar ? `<img src="${cfg.botAvatar}" class="bot-avatar">` : ""}
          <div class="message-content" style="background:${cfg.themeColor};color:${cfg.textColor}">
            ${cfg.welcomeMessage}
          </div>
        </div>
      </div>
      <div class="input-area">
        <input type="text" id="agent_input" placeholder="Type your message…">
        <button id="send_btn" style="color:${cfg.themeColor}">➤</button>
        <button id="mic_btn" style="color:${cfg.themeColor}">🎤</button>
        <audio id="player" hidden></audio>
      </div>
    </div>`;
  document.body.appendChild(widget);

  /* ===== 4. HELPERS ===== */
  const $ = s => widget.querySelector(s);
  const input   = $("#agent_input"),
        sendBtn = $("#send_btn"),
        list    = $("#message_list"),
        close   = widget.querySelector(".agent-close"),
        player  = $("#player"),
        micBtn  = $("#mic_btn");

  function addMessage(txt, type="user") {
    const wrap = document.createElement("div");
    wrap.className = `message ${type}`;
    if (type === "bot" && cfg.botAvatar) {
      wrap.innerHTML = `<img src="${cfg.botAvatar}" class="bot-avatar"><div class="message-content" style="background:${cfg.themeColor};color:${cfg.textColor}">${txt}</div>`;
    } else {
      wrap.innerHTML = `<div class="message-content">${txt}</div>`;
    }
    list.appendChild(wrap);
    list.scrollTop = list.scrollHeight;
  }

  /* ===== 5. CHAT ===== */
  let conversationId = "0";

  async function send(txt) {
    addMessage(txt, "user");
    try {
      const res = await fetch(cfg.apiUrl, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ botId: cfg.botId, message: txt, conversationId })
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      conversationId = data.conversationId || conversationId;
      addMessage(data.message ?? "(no reply)", "bot");
      if (voiceModeEnabled && data.audio) {
        playAudio(data.audio); // backend already returns base64 or URL
      } else if (voiceModeEnabled && data.message) {
        // fallback if backend didn't send audio
        speak(data.message);
      }
    } catch (err) {
      console.error(err);
      addMessage("Sorry, I couldn’t reach the server.", "bot");
    }
  }

  /* ===== 6. AUDIO (backend) ===== */
  async function speak(text) {
    try {
      const url = `${cfg.ttsUrl}?text=${encodeURIComponent(text)}&voiceId=${cfg.voiceId}`;
      const res = await fetch(url, { method: "GET" });
      if (!res.ok) throw new Error(`TTS HTTP ${res.status}`);
      const audioBlob = await res.blob();
      player.src = URL.createObjectURL(audioBlob);
      await player.play();
    } catch (e) {
      console.error("TTS error", e);
    }
  }

  function playAudio(base64) {
    // if backend returns base64 string
    if (base64.startsWith("data:")) {
      player.src = base64;
    } else {
      player.src = `data:audio/mpeg;base64,${base64}`;
    }
    player.play();
  }

  /* ===== 7. VOICE MODE ===== */
  let voiceModeEnabled = false;   // toggled with mic button
  let rec, chunks = [], silenceTimer, analyser, audioCtx;
  const SILENCE_MS = 1000;        // stop after 1s silence
  const THRESHOLD  = 0.02;        // audio level threshold

  function startRecording() {
    navigator.mediaDevices.getUserMedia({ audio: true }).then(stream => {
      chunks = [];
      rec = new MediaRecorder(stream);
      rec.ondataavailable = e => chunks.push(e.data);
      rec.onstop = onRecordingStop;
      rec.start();

      // === SILENCE DETECTION ===
      audioCtx = new (window.AudioContext || window.webkitAudioContext)();
      const source = audioCtx.createMediaStreamSource(stream);
      analyser = audioCtx.createAnalyser();
      source.connect(analyser);
      const data = new Uint8Array(analyser.fftSize);
      let lastSound = Date.now();
      silenceTimer = setInterval(() => {
        analyser.getByteTimeDomainData(data);
        const norm = Math.max(...data) / 128 - 1; // -1..1 range
        if (Math.abs(norm) > THRESHOLD) lastSound = Date.now();
        if (Date.now() - lastSound > SILENCE_MS) {
          clearInterval(silenceTimer);
          rec.stop();
          stream.getTracks().forEach(t => t.stop());
          if (audioCtx) audioCtx.close();
        }
      }, 200);
    }).catch(console.error);
  }

  async function onRecordingStop() {
    const blob = new Blob(chunks, { type: "audio/webm" });
    if (blob.size < 1000) {           // too small, ignore
      if (voiceModeEnabled) startRecording();
      return;
    }
    const fd = new FormData();
    fd.append("file", blob, "speech.webm");
    try {
      const r = await fetch(cfg.sttUrl, { method: "POST", body: fd });
      if (!r.ok) throw new Error(`STT HTTP ${r.status}`);
      const { text } = await r.json();
      if (text) send(text);
    } catch (err) { console.error("STT error", err); }
  }

  // Auto restart listening after TTS playback ends
  player.addEventListener("ended", () => {
    if (voiceModeEnabled) startRecording();
  });

  /* ===== 8. UI EVENTS ===== */
  launcher.onclick = () => widget.style.display = widget.style.display === "none" ? "flex" : "none";
  close.onclick   = () => (widget.style.display = "none");
  sendBtn.onclick = () => {
    const txt = input.value.trim();
    if (!txt) return;
    input.value = "";
    send(txt);
  };
  input.addEventListener("keypress", e => { if (e.key === "Enter") {
    const txt = input.value.trim();
    if (!txt) return;
    input.value = "";
    send(txt);
  }});

  // Mic toggle
  micBtn.onclick = () => {
    voiceModeEnabled = !voiceModeEnabled;
    micBtn.textContent = voiceModeEnabled ? "⏹" : "🎤";
    if (voiceModeEnabled) {
      startRecording();
    } else {
      if (rec && rec.state === "recording") rec.stop();
      clearInterval(silenceTimer);
    }
  };
})();
