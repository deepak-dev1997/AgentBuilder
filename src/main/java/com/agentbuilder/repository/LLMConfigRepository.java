package com.agentbuilder.repository;

import com.agentbuilder.model.LLMConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LLMConfigRepository extends MongoRepository<LLMConfig, String> {}