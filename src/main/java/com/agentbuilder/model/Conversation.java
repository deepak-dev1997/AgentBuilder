package com.agentbuilder.model;


import com.agentbuilder.dtos.openairequests.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversation")
public class Conversation {

    @Id
    private String id;
    private List<Message> messages;

    public Conversation(String message){
        this.messages= new ArrayList<Message>();
        this.messages.add(new Message("user",message));
    }

}
