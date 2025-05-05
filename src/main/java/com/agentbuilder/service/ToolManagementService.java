package com.agentbuilder.service;

import com.agentbuilder.exception.ResourceNotFoundException;
import com.agentbuilder.model.BotConfig;
import com.agentbuilder.model.ToolConfig;
import com.agentbuilder.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.List;

@Service
public class ToolManagementService {
    private final BotRepository botRepo;

    @Autowired
    public ToolManagementService(BotRepository botRepo) {
        this.botRepo = botRepo;
    }

    public List<ToolConfig> listTools(String botName) {
        BotConfig bot = botRepo.findById(botName)
                .orElseThrow(() -> new ResourceNotFoundException("Bot not found: " + botName));
        return bot.getTools();
    }

    public ToolConfig getTool(String botName, String toolName) {
        return listTools(botName).stream()
                .filter(t -> t.getToolName().equals(toolName))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Tool not found: " + toolName));
    }

    public BotConfig addTool(String botName, ToolConfig tool) {
        BotConfig bot = botRepo.findById(botName)
                .orElseThrow(() -> new ResourceNotFoundException("Bot not found: " + botName));
        bot.getTools().add(tool);
        return botRepo.save(bot);
    }

    public void updateTool(String botName, String toolName, ToolConfig tool) {
        BotConfig bot = botRepo.findById(botName)
                .orElseThrow(() -> new ResourceNotFoundException("Bot not found: " + botName));

        List<ToolConfig> tools = bot.getTools();
        OptionalInt idx = IntStream.range(0, tools.size())
                .filter(i -> tools.get(i).getToolName().equals(toolName))
                .findFirst();

        if (idx.isEmpty()) {
            throw new ResourceNotFoundException("Tool not found: " + toolName);
        }

        tools.set(idx.getAsInt(), tool);
        botRepo.save(bot);
    }

    public void deleteTool(String botName, String toolName) {
        BotConfig bot = botRepo.findById(botName)
                .orElseThrow(() -> new ResourceNotFoundException("Bot not found: " + botName));

        boolean removed = bot.getTools().removeIf(t -> t.getToolName().equals(toolName));
        if (!removed) {
            throw new ResourceNotFoundException("Tool not found: " + toolName);
        }
        botRepo.save(bot);
    }
}
