package com.agentbuilder.service;

import com.agentbuilder.Utils.ApiUtil;
import com.agentbuilder.dtos.ChatRequest;
import com.agentbuilder.dtos.openairequests.ChatCompletionDto;
import com.agentbuilder.dtos.openairequests.ChatResponse;
import com.agentbuilder.dtos.openairequests.Message;
import com.agentbuilder.helper.Qna;
import com.agentbuilder.model.*;

import com.agentbuilder.repository.ConversationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.tools.Tool;
import java.util.*;

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

    @Autowired
    private ConversationRepository conversationRepository;

    @Value("${spring.data.mongodb.database}")
    private String db;

    @Value("${ai.vectordb}")
    private String collectionName;

    public ChatResponse chat(ChatRequest chatRequest){
        Optional<Conversation> conversationOptional = conversationRepository.findById(chatRequest.getConversationId());
        Conversation conversation = null;
        if(conversationOptional.isEmpty()){
            conversation = new Conversation(chatRequest.getMessage());
            conversation = conversationRepository.save(conversation);

        }else{
            conversation = conversationOptional.get();
            conversation.getMessages().add(new Message("user",chatRequest.getMessage()));
        }

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
            sb.append("You need to act as chatbot as instructed by the user and follow the instructions given below, you will be provided with the tools, if you feel and based on the isntruction, you can use it as and when needed., you should always respond back in json format, where key should be response: value should be either plain text message or null, toolName: value should be name of the tool which you want to use or it should be empty.\n You cannot send both tools and response , it can be either tools or response one of them needs to be null.\nhere is the source of knowledge for you, either a plausible reply should be there or a tool to use and this rule you need to follow with utmost honesty, you cannot cheat like keep the reply empty string and tools as null . either one of them should be real one. so that conversation is going on either by tool or by chat. use tools only and only when needed and you think you can suffice tool otherwise chat with user");
            for(int a =0;a<documents.size();a++ ){
            	
                sb.append("source:"+documents.get(a).getDocumentContent()+"\n");
            }
            for(Qna qna : botConfig.getKnowledgeBase().getQnas()) {
            	sb.append("source:\n question: "+ qna.getQuestion()+"\n Asnwer: "+qna.getAnswer()+"\n");
            }
            sb.append("these are the instructions given by user that you need to act as a : " +promptConfig.getRole()+"\n here are further commands "+ promptConfig.getSystemPrompt());
            List<Message> messages = new ArrayList<>();

            sb.append("these are the tools that you can use also try to understand there use case:\n");
            for(ToolConfig toolConfig: botConfig.getTools()){
                sb.append("tootName: "+toolConfig.getToolName());
                sb.append("description/usecase of tool: "+toolConfig.getDescription());
            }
            messages.add(new Message("system",sb.toString()));
            for(Message m : conversation.getMessages()){
                messages.add(m);
            }
            String jsonResponse = openAIService.jsonResponseString(llmConfig.getModel(),llmConfig.getTemperature(),messages);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root     = mapper.readTree(jsonResponse);
            String reply      = root.get("response").asText();
            if(reply == null || reply.equals("") || reply.isEmpty() || reply.equals("null")){
                String tool = root.get("toolName").asText();
                ToolConfig toUse = null;
                for(ToolConfig configs : botConfig.getTools()){
                    if(tool.equals(configs.getToolName())){
                        toUse=configs;
                    }
                }
                List<Message> messages1 = new ArrayList<>();
                StringBuilder sb2 = new StringBuilder();
                sb2.append("You need to act as a toolConfig generator , You will be given the structure of the toolConfig object that we are expecting also a small conversation between user and an chatbot, based on that history you needto prepare a proper Tool config object, always respond back in json and the response should be with same keys as we are givin in sample\n");
                sb2.append("here is the complete conversation : " );
                for(Message m : conversation.getMessages()){
                    sb2.append("role: "+m.getRole()+", message: " + m.getContent());
                }
                sb2.append("here is the tool that we are expecting so create a tool config based on chat history please create properly request body and parameters properly fill out those: " + toUse.toString());
                messages1.add(new Message("system", sb2.toString()));
                String toolsResponse =  openAIService.jsonResponseString(llmConfig.getModel(),llmConfig.getTemperature(),messages1);
                System.out.println(toolsResponse);
                ToolConfig finalToolConfig = mapper.readValue(toolsResponse, ToolConfig.class);
                String apiCallResponse = ApiUtil.callApi(finalToolConfig.getServerUrl(), finalToolConfig.getParameters(),finalToolConfig.getHeaders(),finalToolConfig.getRequestBody());
                reply = toUse.getAfterTool();
                ChatResponse chatResponse = new ChatResponse();
                chatResponse.setMessage(reply);
                chatResponse.setVoiceId(botConfig.getVoiceId());
                reply+= "here is api call response - "+apiCallResponse;
                conversation.getMessages().add(new Message("assistant",reply));
                chatResponse.setConversationId(conversation.getId());
                return chatResponse;

            }
            ChatResponse chatResponse = new ChatResponse();
            chatResponse.setMessage(reply);
            conversation.getMessages().add(new Message("assistant",reply));
            chatResponse.setConversationId(conversation.getId());
            return chatResponse;
        }catch (Exception e){
            e.printStackTrace();
            ChatResponse chatResponse= new ChatResponse();
            chatResponse.setResponse(e.getMessage());
            conversation.getMessages().add(new Message("assistant","Some error occured"));
            return chatResponse;
        }finally {
            conversationRepository.save(conversation);
        }

    }

}
