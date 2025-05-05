package com.agentbuilder.controller;

import com.agentbuilder.model.PromptConfig;
import com.agentbuilder.service.PromptConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/prompt-configs")
@CrossOrigin("*")
public class PromptConfigController {
    private final PromptConfigService service;

    @Autowired
    public PromptConfigController(PromptConfigService service) {
        this.service = service;
    }

    @GetMapping
    public List<PromptConfig> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PromptConfig getById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromptConfig create(@RequestBody PromptConfig config) {
        return service.create(config);
    }

    @PutMapping("/{id}")
    public PromptConfig update(@PathVariable String id, @RequestBody PromptConfig config) {
        return service.update(id, config);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
