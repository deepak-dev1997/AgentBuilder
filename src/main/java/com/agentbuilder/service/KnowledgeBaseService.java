package com.agentbuilder.service;

import com.agentbuilder.Utils.DocumentTextExtractor;
import com.agentbuilder.exception.ResourceNotFoundException;
import com.agentbuilder.helper.FileDetails;
import com.agentbuilder.helper.Qna;
import com.agentbuilder.model.BotConfig;
import com.agentbuilder.model.KnowledgeBase;
import com.agentbuilder.model.VectorDocuments;
import com.agentbuilder.repository.BotRepository;
import com.agentbuilder.repository.VectorDocumentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KnowledgeBaseService {

    private final BotRepository botRepo;

    private final OpenAIService openAIService;

    private final VectorDocumentsRepository vectorDocumentsRepository;
    /* ---------- read ---------- */

    public KnowledgeBase getKb(String botName) {
        BotConfig bot = botRepo.findById(botName)
                .orElseThrow(() -> new ResourceNotFoundException("Bot " + botName + " not found"));
        return bot.getKnowledgeBase();
    }

    /* ---------- QnA ---------- */

    public KnowledgeBase addQna(String botName, Qna dto) {
        BotConfig bot = ensureKb(botName);
        bot.getKnowledgeBase().getQnas().add(dto);
        return botRepo.save(bot).getKnowledgeBase();
    }

    public KnowledgeBase updateQna(String botName, int index, Qna dto) {
        BotConfig bot = ensureKb(botName);
        bot.getKnowledgeBase().getQnas().set(index, dto);
        return botRepo.save(bot).getKnowledgeBase();
    }

    public KnowledgeBase deleteQna(String botName, int index) {
        BotConfig bot = ensureKb(botName);
        bot.getKnowledgeBase().getQnas().remove(index);
        return botRepo.save(bot).getKnowledgeBase();
    }

    /* ---------- files ---------- */

    public KnowledgeBase addFile(String botName, MultipartFile file) throws IOException {

        String id = UUID.randomUUID().toString();
        String fileText = DocumentTextExtractor.extractText(file);
        fileText = openAIService.preprocessText(fileText);
        List<String> chunks = openAIService.getChunks(fileText);
        List<VectorDocuments> vectorDocumentsList = new ArrayList<>();
        for(String s : chunks){
            VectorDocuments vectorDocuments = new VectorDocuments();
            vectorDocuments.setDocumentContent(s);
            vectorDocuments.setDocumentName(file.getOriginalFilename());
            vectorDocuments.setPage(0);
            vectorDocuments.setEmbeddings(openAIService.createEmbedding(s));
            vectorDocuments.setSiteId(botName);
            vectorDocuments.setDocumentId(id);
            vectorDocumentsList.add(vectorDocuments);
        }
        vectorDocumentsRepository.saveAll(vectorDocumentsList);


        BotConfig bot = ensureKb(botName);
        bot.getKnowledgeBase().getFileDetails()
                .add(new FileDetails(file.getOriginalFilename(), id));
        return botRepo.save(bot).getKnowledgeBase();
    }

    public KnowledgeBase deleteFile(String botName, String fileId) throws IOException {
        BotConfig bot = ensureKb(botName);
        Iterator<FileDetails> it = bot.getKnowledgeBase().getFileDetails().iterator();
        while (it.hasNext()) {
            FileDetails fd = it.next();
            if (fd.getFileId().equals(fileId)) {
                Path p = Paths.get("uploads", botName).resolve(fd.getFileId() + "-" + fd.getFileName());
                it.remove();
            }
        }
        vectorDocumentsRepository.deleteByDocumentId(fileId);
        return botRepo.save(bot).getKnowledgeBase();
    }

    /* ---------- helpers ---------- */

    private BotConfig ensureKb(String botName) {
        BotConfig bot = botRepo.findById(botName)
                .orElseThrow(() -> new ResourceNotFoundException("Bot " + botName + " not found"));
        if (bot.getKnowledgeBase() == null) {
            bot.setKnowledgeBase(new KnowledgeBase(new ArrayList<>(), new ArrayList<>()));
        }
        return bot;
    }

    private Qna toEntity(Qna dto) {
        Qna q = new Qna();
        q.setQuestion(dto.getQuestion());
        q.setAnswer(dto.getAnswer());
        return q;
    }
}