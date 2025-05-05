package com.agentbuilder.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.agentbuilder.dtos.BotDto;
import com.agentbuilder.model.BotConfig;
import com.agentbuilder.service.BotService;

@RestController
@RequestMapping("/api/bots")
@CrossOrigin("*")
public class BotController {

    @Autowired
    private BotService service;

    @GetMapping
    public List<BotConfig> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public BotConfig getById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BotConfig create(@RequestBody BotDto config) {
        return service.create(config);
    }

    @PutMapping("/{id}")
    public BotConfig update(@PathVariable String id, @RequestBody BotDto config) {
        return service.update(id, config);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}

