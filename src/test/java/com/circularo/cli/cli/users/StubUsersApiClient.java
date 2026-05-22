package com.circularo.cli.cli.users;

import com.circularo.cli.api.UsersApiClient;
import com.circularo.cli.model.User;
import com.circularo.cli.model.UserModels;

import java.util.List;
import java.util.Map;

/**
 * AOT-friendly stub for UsersApiClient.
 */
class StubUsersApiClient extends UsersApiClient {
    private List<User> stubUsers;
    private User stubUser;
    private UserModels.GenericStatus stubGenericStatus;
    private UserModels.BulkResponse stubBulkResponse;
    private List<UserModels.UserRightItem> stubUserRights;
    private UserModels.LoginResponse stubLoginResponse;
    private UserModels.UserActivation stubUserActivation;
    private UserModels.UserResetToken stubUserResetToken;
    private UserModels.PasswordStrength stubPasswordStrength;
    private UserModels.UserConfiguration stubUserConfiguration;
    private RuntimeException exceptionToThrow;

    // Capture arguments
    String lastId;
    String lastReassign;
    UserModels.CreateUserRequest lastCreateRequest;
    UserModels.UpdateUsersBulkRequest lastUpdateBulkRequest;
    UserModels.UpdateUserRequest lastUpdateRequest;
    Boolean lastDisableActiveTokens;
    Integer lastVersion;
    String lastUsername;
    String lastPhotoSize;
    Boolean lastDetailed;
    Map<String, String> lastFields;
    byte[] lastPhotoBytes;
    String lastPhotoFilename;
    String lastTenant;
    UserModels.ChangePasswordRequest lastChangePasswordRequest;
    String lastPassword;
    UserModels.ResetPasswordRequest lastResetPasswordRequest;
    String lastResetToken;
    String lastGroupname;
    UserModels.AddCertificateRequest lastAddCertificateRequest;
    String lastCertificateId;
    UserModels.RegisterUserRequest lastRegisterUserRequest;

    public StubUsersApiClient() {
        super(null);
    }

    public void setStubUsers(List<User> users) {
        this.stubUsers = users;
    }

    public void setStubUser(User user) {
        this.stubUser = user;
    }

    public void setStubGenericStatus(UserModels.GenericStatus status) {
        this.stubGenericStatus = status;
    }

    public void setStubBulkResponse(UserModels.BulkResponse bulkResponse) {
        this.stubBulkResponse = bulkResponse;
    }

    public void setStubUserRights(List<UserModels.UserRightItem> rights) {
        this.stubUserRights = rights;
    }

    public void setStubLoginResponse(UserModels.LoginResponse loginResponse) {
        this.stubLoginResponse = loginResponse;
    }

    public void setStubUserActivation(UserModels.UserActivation activation) {
        this.stubUserActivation = activation;
    }

    public void setStubUserResetToken(UserModels.UserResetToken resetToken) {
        this.stubUserResetToken = resetToken;
    }

    public void setStubPasswordStrength(UserModels.PasswordStrength strength) {
        this.stubPasswordStrength = strength;
    }

    public void setStubUserConfiguration(UserModels.UserConfiguration config) {
        this.stubUserConfiguration = config;
    }

    public void setException(RuntimeException ex) {
        this.exceptionToThrow = ex;
    }

    @Override
    public List<User> searchUsers(String query, int limit) {
        if (exceptionToThrow != null) {
            throw exceptionToThrow;
        }
        return stubUsers;
    }

    @Override
    public User getUser(String id) {
        this.lastId = id;
        return stubUser;
    }

    @Override
    public UserModels.GenericStatus deleteUser(String id, String reassign) {
        this.lastId = id;
        this.lastReassign = reassign;
        return stubGenericStatus;
    }

    @Override
    public UserModels.BulkResponse createUsers(UserModels.CreateUserRequest requestPayload) {
        this.lastCreateRequest = requestPayload;
        return stubBulkResponse;
    }

    @Override
    public UserModels.BulkResponse updateUsersBulk(UserModels.UpdateUsersBulkRequest requestPayload) {
        this.lastUpdateBulkRequest = requestPayload;
        return stubBulkResponse;
    }

    @Override
    public UserModels.GenericStatus updateUser(String id, UserModels.UpdateUserRequest requestPayload, Boolean disableActiveTokens, Integer version) {
        this.lastId = id;
        this.lastUpdateRequest = requestPayload;
        this.lastDisableActiveTokens = disableActiveTokens;
        this.lastVersion = version;
        return stubGenericStatus;
    }

    @Override
    public User getUserByUsername(String username, String photoSize) {
        this.lastUsername = username;
        this.lastPhotoSize = photoSize;
        return stubUser;
    }

    @Override
    public List<UserModels.UserRightItem> getUserRights(String id, Boolean detailed) {
        this.lastId = id;
        this.lastDetailed = detailed;
        return stubUserRights;
    }

    @Override
    public UserModels.LoginResponse activateUser(Map<String, String> fields, byte[] photoBytes, String photoFilename) {
        this.lastFields = fields;
        this.lastPhotoBytes = photoBytes;
        this.lastPhotoFilename = photoFilename;
        return stubLoginResponse;
    }

    @Override
    public UserModels.GenericStatus resendActivation(String id, String tenant) {
        this.lastId = id;
        this.lastTenant = tenant;
        return stubGenericStatus;
    }

    @Override
    public UserModels.UserActivation checkActivationToken() {
        return stubUserActivation;
    }

    @Override
    public UserModels.GenericStatus changePassword(String id, UserModels.ChangePasswordRequest requestPayload) {
        this.lastId = id;
        this.lastChangePasswordRequest = requestPayload;
        return stubGenericStatus;
    }

    @Override
    public UserModels.PasswordStrength checkPasswordStrength(String password, String tenant) {
        this.lastPassword = password;
        this.lastTenant = tenant;
        return stubPasswordStrength;
    }

    @Override
    public UserModels.GenericStatus resetPassword(UserModels.ResetPasswordRequest requestPayload) {
        this.lastResetPasswordRequest = requestPayload;
        return stubGenericStatus;
    }

    @Override
    public UserModels.GenericStatus resetPasswordUsername(String username, String tenant) {
        this.lastUsername = username;
        this.lastTenant = tenant;
        return stubGenericStatus;
    }

    @Override
    public UserModels.UserResetToken checkResetToken(String resetToken) {
        this.lastResetToken = resetToken;
        return stubUserResetToken;
    }

    @Override
    public UserModels.GenericStatus addUserToGroup(String id, String groupname) {
        this.lastId = id;
        this.lastGroupname = groupname;
        return stubGenericStatus;
    }

    @Override
    public UserModels.GenericStatus removeUserFromGroup(String id, String groupname) {
        this.lastId = id;
        this.lastGroupname = groupname;
        return stubGenericStatus;
    }

    @Override
    public UserModels.UserConfiguration uploadPhoto(String id, byte[] photoBytes, String filename) {
        this.lastId = id;
        this.lastPhotoBytes = photoBytes;
        this.lastPhotoFilename = filename;
        return stubUserConfiguration;
    }

    @Override
    public UserModels.GenericStatus deletePhoto(String id) {
        this.lastId = id;
        return stubGenericStatus;
    }

    @Override
    public UserModels.GenericStatus deleteSignature(String id) {
        this.lastId = id;
        return stubGenericStatus;
    }

    @Override
    public UserModels.GenericStatus deleteInitials(String id) {
        this.lastId = id;
        return stubGenericStatus;
    }

    @Override
    public UserModels.GenericStatus addCertificate(String id, UserModels.AddCertificateRequest requestPayload) {
        this.lastId = id;
        this.lastAddCertificateRequest = requestPayload;
        return stubGenericStatus;
    }

    @Override
    public UserModels.GenericStatus removeCertificate(String id, String certificateId) {
        this.lastId = id;
        this.lastCertificateId = certificateId;
        return stubGenericStatus;
    }

    @Override
    public UserModels.GenericStatus registerUser(UserModels.RegisterUserRequest requestPayload) {
        this.lastRegisterUserRequest = requestPayload;
        return stubGenericStatus;
    }
}
