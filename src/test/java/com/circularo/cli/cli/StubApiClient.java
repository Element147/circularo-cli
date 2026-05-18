package com.circularo.cli.cli;

import com.circularo.cli.api.CircularoApiClient;
import com.circularo.cli.model.User;
import com.circularo.cli.model.Document;

import java.net.http.HttpClient;
import java.util.List;

/**
 * Lightweight compile-time stub double for CircularoApiClient.
 * Avoids any dynamic Mockito proxy reflection bottlenecks, making unit tests
 * 100% native image compatible.
 */
class StubApiClient extends CircularoApiClient {
    private List<User> stubUsers;
    private List<Document> stubDocs;
    private RuntimeException exceptionToThrow;

    public StubApiClient() {
        super(null, (HttpClient) null);
    }

    public void setStubUsers(List<User> users) {
        this.stubUsers = users;
    }

    public void setStubDocs(List<Document> docs) {
        this.stubDocs = docs;
    }

    public void setException(RuntimeException ex) {
        this.exceptionToThrow = ex;
    }

    @Override
    public List<User> searchUsers(String query) {
        if (exceptionToThrow != null) {
            throw exceptionToThrow;
        }
        return stubUsers;
    }

    @Override
    public List<Document> searchDocuments(String query) {
        if (exceptionToThrow != null) {
            throw exceptionToThrow;
        }
        return stubDocs;
    }
}
