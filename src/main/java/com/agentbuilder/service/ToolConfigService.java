//package com.agentbuilder.service;
//
//import com.agentbuilder.exception.ResourceNotFoundException;
//import com.agentbuilder.model.ToolConfig;
//import com.agentbuilder.repository.ToolConfigRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ToolConfigService {
//    private final ToolConfigRepository repository;
//
//    @Autowired
//    public ToolConfigService(ToolConfigRepository repository) {
//        this.repository = repository;
//    }
//
//    public List<ToolConfig> findAll() {
//        return repository.findAll();
//    }
//
//    public ToolConfig findById(String id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("ToolConfig not found with id " + id));
//    }
//
//    public ToolConfig create(ToolConfig config) {
//        return repository.save(config);
//    }
//
//    public ToolConfig update(String id, ToolConfig config) {
//        ToolConfig existing = findById(id);
//        existing.setToolName(config.getToolName());
//        existing.setDescription(config.getDescription());
//        existing.setHeaders(config.getHeaders());
//        existing.setServerUrl(config.getServerUrl());
//        existing.setParameters(config.getParameters());
//        existing.setRequestBody(config.getRequestBody());
//        existing.setBeforeTool(config.getBeforeTool());
//        existing.setAfterTool(config.getAfterTool());
//        return repository.save(existing);
//    }
//
//    public void delete(String id) {
//        repository.deleteById(id);
//    }
//}
