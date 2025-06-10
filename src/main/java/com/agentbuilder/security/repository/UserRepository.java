package com.agentbuilder.security.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.agentbuilder.security.models.User;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}