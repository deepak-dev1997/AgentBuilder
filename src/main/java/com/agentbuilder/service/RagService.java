package com.agentbuilder.service;

import com.agentbuilder.dtos.ChatRequest;
import com.agentbuilder.dtos.openairequests.ChatResponse;
import com.agentbuilder.dtos.openairequests.Message;
import com.agentbuilder.model.BotConfig;
import com.agentbuilder.model.LLMConfig;
import com.agentbuilder.model.PromptConfig;
import com.agentbuilder.model.VectorDocuments;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class RagService {

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private BotService botService;

    @Autowired
    private PromptConfigService promptConfigService;

    @Autowired
    private LLMConfigService llmConfigService;

    @Value("${spring.data.mongodb.database}")
    private String db;

    @Value("${ai.vectordb}")
    private String collectionName;

    public ChatResponse chat(ChatRequest chatRequest){
        try {
            BotConfig botConfig = botService.findById(chatRequest.getBotId());
            PromptConfig promptConfig = promptConfigService.findById(chatRequest.getBotId());
            LLMConfig llmConfig = llmConfigService.findById(chatRequest.getBotId());
            List<Double> queryEmbedding = openAIService.createEmbedding(chatRequest.getMessage());
            MongoDatabase database=mongoClient.getDatabase(db);
            MongoCollection<Document> collection = database.getCollection(collectionName);
            int numCandidates = 100;
            int limit = 3;
            Document filter = new Document("$and", Arrays.asList(
                    new Document("siteId", new Document("$in", Collections.singletonList(chatRequest.getBotId())))));
            Bson vectorSearchStage = new Document("$vectorSearch",
                    new Document().append("index", "vector_index").append("path", "embeddings").append("filter", filter)
                            .append("queryVector", queryEmbedding).append("numCandidates", numCandidates)
                            .append("limit", limit));
            List<Bson> aggregationPipeline = Collections.singletonList(vectorSearchStage);
            AggregateIterable<Document> result = collection.aggregate(aggregationPipeline);
            List<VectorDocuments> documents = new ArrayList<>();
            for (var doc : result) {
                JSONObject jsonObject = new JSONObject(doc.toJson());

                VectorDocuments vectorDocuments = new VectorDocuments();
                vectorDocuments.setDocumentId(jsonObject.optString("documentId", null));
                vectorDocuments.setDocumentContent(jsonObject.optString("documentContent", null));
                vectorDocuments.setPage(jsonObject.optInt("page", 0));
                vectorDocuments.setDocumentName(jsonObject.optString("documentName", null));

                JSONObject idObject = jsonObject.optJSONObject("_id");
                if (idObject != null) {
                    vectorDocuments.setId(idObject.optString("$oid", null));
                }

                documents.add(vectorDocuments);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("You need to act as chatbot as instructed by the user, Your job is to try to answer the user's query based on the knowledgebase provided and never go beyond the knowledgebase, always respond back in json format where key should be response and value should be the answer\nhere is the source of knowledge for you,");
            for(int a =0;a<documents.size();a++ ){
            	
                sb.append("source:"+documents.get(a).getDocumentContent()+"\n");
            }
            sb.append("these are the instructions given by user that you need to act as a : " +promptConfig.getRole()+"\n here are further commands "+ promptConfig.getSystemPrompt());
            List<Message> messages = new ArrayList<>();
            messages.add(new Message("system",sb.toString()));
            messages.add(new Message("user", chatRequest.getMessage()));
            String jsonResponse = openAIService.jsonResponseString(llmConfig.getModel(),llmConfig.getTemperature(),messages);
            ChatResponse chatResponse = new ChatResponse(jsonResponse);

            return chatResponse;
        }catch (Exception e){
            e.printStackTrace();
            ChatResponse chatResponse= new ChatResponse();
            chatResponse.setResponse(e.getMessage());
            return chatResponse;
        }

    }

}
