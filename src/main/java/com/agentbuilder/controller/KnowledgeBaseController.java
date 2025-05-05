package com.agentbuilder.controller;

import com.agentbuilder.helper.Qna;
import com.agentbuilder.model.KnowledgeBase;
import com.agentbuilder.service.KnowledgeBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/bots/{botName}/kb")
@RequiredArgsConstructor
@CrossOrigin("*")
public class KnowledgeBaseController {

    private final KnowledgeBaseService kbService;

    /* ---------- whole KB ---------- */
    @GetMapping
    public KnowledgeBase getKb(@PathVariable String botName) {
        return toDto(kbService.getKb(botName));
    }

    /* ---------- QnA ---------- */

    @PostMapping("/qnas")
    public KnowledgeBase addQna(@PathVariable String botName, @RequestBody Qna dto) {
        return toDto(kbService.addQna(botName, dto));
    }

    @PutMapping("/qnas/{index}")
    public KnowledgeBase updateQna(@PathVariable String botName,
                                      @PathVariable int index,
                                      @RequestBody Qna dto) {
        return toDto(kbService.updateQna(botName, index, dto));
    }

    @DeleteMapping("/qnas/{index}")
    public KnowledgeBase deleteQna(@PathVariable String botName, @PathVariable int index) {
        return toDto(kbService.deleteQna(botName, index));
    }

    /* ---------- file upload ---------- */

    @PostMapping("/files")
    public KnowledgeBase uploadFile(@PathVariable String botName,
                                       @RequestPart("file") MultipartFile file) throws IOException {
        return toDto(kbService.addFile(botName, file));
    }

    @DeleteMapping("/files/{fileId}")
    public KnowledgeBase deleteFile(@PathVariable String botName,
                                       @PathVariable String fileId) throws IOException {
        return toDto(kbService.deleteFile(botName, fileId));
    }

    /* ---------- helper ---------- */
    private KnowledgeBase toDto(KnowledgeBase kb) {
        return new KnowledgeBase(kb.getQnas(), kb.getFileDetails());
    }
}