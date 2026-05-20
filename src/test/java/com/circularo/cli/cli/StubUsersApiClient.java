package com.circularo.cli.cli;

import com.circularo.cli.api.UsersApiClient;
import com.circularo.cli.model.User;

import java.util.List;

/**
 * AOT-friendly stub for UsersApiClient.
 */
class StubUsersApiClient extends UsersApiClient {
    private List<User> stubUsers;
    private RuntimeException exceptionToThrow;

    public StubUsersApiClient() {
        super(null);
    }

    public void setStubUsers(List<User> users) {
        this.stubUsers = users;
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
}
