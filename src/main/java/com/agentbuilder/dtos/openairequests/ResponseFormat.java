package com.agentbuilder.dtos.openairequests;

import lombok.Data;

@Data
public class ResponseFormat {

    private String type;

    public ResponseFormat(String type) {
        super();
        this.type = type;
    }


}
