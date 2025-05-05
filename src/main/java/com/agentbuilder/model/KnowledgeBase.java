package com.agentbuilder.model;

import com.agentbuilder.helper.FileDetails;
import com.agentbuilder.helper.Qna;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class KnowledgeBase {

    private List<Qna> qnas;
    private List<FileDetails> fileDetails;
    public KnowledgeBase(){
        this.qnas = new ArrayList<Qna>();
        this.fileDetails= new ArrayList<FileDetails>();
    }

}
