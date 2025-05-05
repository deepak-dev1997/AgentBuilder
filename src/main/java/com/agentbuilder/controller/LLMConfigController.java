package com.agentbuilder.controller;

import com.agentbuilder.model.LLMConfig;
import com.agentbuilder.service.LLMConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/llm-configs")
@CrossOrigin("*")
public class LLMConfigController {
    private final LLMConfigService service;

    @Autowired
    public LLMConfigController(LLMConfigService service) {
        this.service = service;
    }

    @GetMapping
    public List<LLMConfig> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public LLMConfig getById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LLMConfig create(@RequestBody LLMConfig config) {
        return service.create(config);
    }

    @PutMapping("/{id}")
    public LLMConfig update(@PathVariable String id, @RequestBody LLMConfig config) {
        return service.update(id, config);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}