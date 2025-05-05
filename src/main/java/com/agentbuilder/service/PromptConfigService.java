package com.agentbuilder.service;

import com.agentbuilder.exception.ResourceNotFoundException;
import com.agentbuilder.model.PromptConfig;
import com.agentbuilder.repository.PromptConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptConfigService {
    private final PromptConfigRepository repository;

    @Autowired
    public PromptConfigService(PromptConfigRepository repository) {
        this.repository = repository;
    }

    public List<PromptConfig> findAll() {
        return repository.findAll();
    }

    public PromptConfig findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PromptConfig not found with id " + id));
    }

    public PromptConfig create(PromptConfig config) {
        return repository.save(config);
    }

    public PromptConfig update(String id, PromptConfig config) {
        PromptConfig existing = findById(id);
        existing.setRole(config.getRole());
        existing.setSystemPrompt(config.getSystemPrompt());
        existing.setFirstMessage(config.getFirstMessage());
        return repository.save(existing);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}
