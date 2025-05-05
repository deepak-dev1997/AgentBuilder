package com.agentbuilder.repository;


import com.agentbuilder.model.VectorDocuments;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VectorDocumentsRepository extends MongoRepository<VectorDocuments, String> {

	void deleteByDocumentId(String documentId);
	
	void deleteBySiteId(String siteId);
	
	void deleteBySiteIdAndDocumentId(String siteId, String documentId);
}
