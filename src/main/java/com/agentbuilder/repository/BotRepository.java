package com.agentbuilder.repository;

import com.agentbuilder.model.BotConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotRepository extends MongoRepository<BotConfig, String> { }

