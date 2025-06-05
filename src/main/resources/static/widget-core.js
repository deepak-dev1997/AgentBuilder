/*  ==== Agent Widget  v1.0 (Modified for window.AgentBuilder) ====  */
(function () {
  /* --- 1. Read configuration from window.AgentBuilder --- */
  const cfg = window.AgentBuilder || {
    botId: "bot",
    botName: "Agent",
    themeColor: "#4CAF50",
    textColor: "#ffffff",
    textFont: "Inter",
    welcomeMessage: "How can I help?",
    botAvatar: ""
  };

  const css = `
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap');
    body{font-family:'Inter',sans-serif}
    #agent_launcher{position:fixed;bottom:20px;right:20px;background:linear-gradient(135deg,#0078ff,#00c6ff);color:#fff;
                    font-size:22px;padding:10px;border-radius:50%;cursor:pointer;z-index:9998;transition:transform .3s}
    #agent_launcher:hover{transform:scale(1.1)}
    #agent_widget{position:fixed;bottom:90px;right:20px;width:340px;background:#fff;border:1px solid #ddd;border-radius:12px;
                  box-shadow:0 8px 25px rgba(0,0,0,.15);z-index:9999;display:flex;flex-direction:column;overflow:hidden;
                  animation:fadeInUp .3s}
    @keyframes fadeInUp{from{opacity:0;transform:translateY(30px)}to{opacity:1;transform:translateY(0)}}
    .agent-header{padding:12px 16px;font-weight:600;display:flex;justify-content:space-between;align-items:center;
                  font-size:15px;border-bottom:1px solid #ddd}
    .agent-close{cursor:pointer;font-size:20px;transition:color .2s}
    .agent-close:hover{color:#000}
    .agent-body{display:flex;flex-direction:column;height:100%}
    .message-container{height:350px;overflow-y:auto;background:#fafafa;padding:12px;display:flex;flex-direction:column;
                       gap:10px;scrollbar-width:thin}
    .message{display:flex;align-items:flex-start;gap:8px;margin-top:6px;font-size:14px;max-width:80%;word-wrap:break-word}
    .message.user{align-self:flex-end;flex-direction:row-reverse}
    .message.user .message-content{background:#efefef;color:#000;padding:8px;border-radius:8px}
    .message.bot{align-self:flex-start}
    .message.bot .message-content{padding:8px;border-radius:8px}
    .bot-avatar{width:24px;height:24px;border-radius:50%}
    .input-area{display:flex;padding:4px;background:#fff;border-top:1px solid #ddd;gap:8px}
    .input-area input{flex:1;padding:10px 12px;border-radius:8px;border:none;font-size:14px;outline:none}
    .input-area button{padding:8px 12px;background:transparent;border:none;border-radius:8px;cursor:pointer;font-size:18px}
    `;
    const style = document.createElement("style");
    style.textContent = css;
    document.head.appendChild(style);

    /* --- 3. Build HTML elements --- */
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
        Agent Chat – ${cfg.botId}
        <span class="agent-close" style="color:${cfg.textColor}">&times;</span>
      </div>
      <div class="agent-body">
        <div class="message-container" id="message_list">
          <div class="message bot">
            ${cfg.botAvatar ? `<img src="${cfg.botAvatar}" class="bot-avatar">` : ""}
            <div class="message-content" style="background:${cfg.themeColor};color:${cfg.textColor}">${cfg.welcomeMessage}</div>
          </div>
        </div>
        <div class="input-area">
          <input type="text" id="agent_input" placeholder="Type your message…">
          <button id="send_btn" style="color:${cfg.themeColor}">➤</button>
        </div>
      </div>`;
    document.body.appendChild(widget);

    /* --- 4. Behaviour --- */
    const $ = sel => widget.querySelector(sel);
    const input       = $("#agent_input");
    const sendBtn     = $("#send_btn");
    const messageList = $("#message_list");
    const closeBtn    = widget.querySelector(".agent-close");

    function addMessage(text, type = "user") {
      const wrap = document.createElement("div");
      wrap.className = `message ${type}`;
      if (type === "bot" && cfg.botAvatar) {
        wrap.innerHTML = `<img src="${cfg.botAvatar}" class="bot-avatar">
                          <div class="message-content" style="background:${cfg.themeColor};color:${cfg.textColor}">${text}</div>`;
      } else {
        wrap.innerHTML = `<div class="message-content">${text}</div>`;
      }
      messageList.appendChild(wrap);
      messageList.scrollTop = messageList.scrollHeight;
    }

    function send() {
      const txt = input.value.trim();
      if (!txt) return;
      addMessage(txt, "user");
      input.value = "";
      setTimeout(() => addMessage("Thanks for your message!", "bot"), 600);
    }

    /* click / enter handlers */
    launcher.onclick     = () => widget.style.display = (widget.style.display === "none" ? "flex" : "none");
    closeBtn.onclick     = () => (widget.style.display = "none");
    sendBtn.onclick      = send;
    input.onkeypress     = e => { if (e.key === "Enter") send(); };
  })();