package com.agentbuilder.repository;

import com.agentbuilder.model.PromptConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PromptConfigRepository extends MongoRepository<PromptConfig, String> {}