package com.agentbuilder.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "VectorDocuments")
public class VectorDocuments {

	@Id
	private String id;

	private String documentName;

	private String documentContent;
	private String siteId;

	
	private List<Double> embeddings;

	private String documentId;
	private int page;

}
