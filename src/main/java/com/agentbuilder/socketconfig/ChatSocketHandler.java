//package com.agentbuilder.socketconfig;
//
//import com.agentbuilder.dtos.ChatRequest;
//import com.agentbuilder.dtos.openairequests.ChatResponse;
//import com.agentbuilder.service.RagService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//
//@Component
//public class ChatSocketHandler extends TextWebSocketHandler {
//
//    @Autowired
//    private RagService ragService;
//
//    @Override
//    public void handleTextMessage(WebSocketSession session, TextMessage msg) throws Exception {
//        ChatRequest req = new ObjectMapper().readValue(msg.getPayload(), ChatRequest.class);
//        ChatResponse res = ragService.chat(req);
//
//
//        session.sendMessage(
//                new TextMessage(new ObjectMapper().writeValueAsString(res))
//        );
//    }
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session){
//
//        try {
//            session.sendMessage(new TextMessage("{\"role\":\"system\",\"message\":\"👋 Connected\"}"));
//        } catch (IOException ignored) {}
//    }
//}
