package com.circularo.cli.cli;

import com.circularo.cli.api.DocumentsApiClient;
import com.circularo.cli.model.Document;

import java.util.List;

/**
 * AOT-friendly stub for DocumentsApiClient.
 */
class StubDocumentsApiClient extends DocumentsApiClient {
    private List<Document> stubDocs;
    private RuntimeException exceptionToThrow;

    public StubDocumentsApiClient() {
        super(null);
    }

    public void setStubDocs(List<Document> docs) {
        this.stubDocs = docs;
    }

    public void setException(RuntimeException ex) {
        this.exceptionToThrow = ex;
    }

    @Override
    public List<Document> searchDocuments(String query, int limit) {
        if (exceptionToThrow != null) {
            throw exceptionToThrow;
        }
        return stubDocs;
    }
}
