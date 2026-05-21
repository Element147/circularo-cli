package com.circularo.cli.api;

import com.circularo.cli.model.User;
import com.circularo.cli.model.UserSearchResponse;
import com.circularo.cli.model.UserModels;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain-specific API client handling user search operations.
 */
@Component
@RegisterReflectionForBinding({
    User.class,
    UserSearchResponse.class,
    UserModels.GenericStatus.class,
    UserModels.BulkResponse.class,
    UserModels.BulkResponseItem.class,
    UserModels.UserRightItem.class,
    UserModels.PasswordStrength.class,
    UserModels.UserActivation.class,
    UserModels.UserResetToken.class,
    UserModels.LoginResponse.class,
    UserModels.UserConfiguration.class,
    UserModels.CreateUserRequest.class,
    UserModels.CreateUserData.class,
    UserModels.UpdateUserRequest.class,
    UserModels.UpdateUsersBulkRequest.class,
    UserModels.ChangePasswordRequest.class,
    UserModels.ResetPasswordRequest.class,
    UserModels.AddCertificateRequest.class,
    UserModels.RegisterUserRequest.class
})
public class UsersApiClient {

    private final BaseCircularoApiClient baseClient;

    @Autowired
    public UsersApiClient(BaseCircularoApiClient baseClient) {
        this.baseClient = baseClient;
    }

    /**
     * Searches for Circularo users using the Elasticsearch syntax query.
     *
     * @param query optional Elasticsearch query_string filter
     * @param limit maximum number of results to return
     */
    public List<User> searchUsers(String query, int limit) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("format", "minimal");
        payload.put("size", limit);
        if (query != null && !query.isBlank()) {
            Map<String, Object> queryMap = new HashMap<>();
            Map<String, Object> filterMap = new HashMap<>();
            Map<String, Object> mustMap = new HashMap<>();
            Map<String, Object> queryStringMap = new HashMap<>();
            queryStringMap.put("query", query);
            mustMap.put("query_string", queryStringMap);
            filterMap.put("must", java.util.List.of(mustMap));
            queryMap.put("query", filterMap);
            payload.put("userQuery", queryMap);
        }
        
        String body = baseClient.toJson(payload);
        HttpRequest request = baseClient.newRequestBuilder("users/search")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
                
        UserSearchResponse searchResponse = baseClient.execute(request, UserSearchResponse.class);
        return searchResponse.results() != null ? searchResponse.results() : java.util.List.of();
    }

    public User getUser(String id) {
        HttpRequest request = baseClient.newRequestBuilder("users/" + URLEncoder.encode(id, StandardCharsets.UTF_8))
                .GET()
                .build();
        return baseClient.execute(request, User.class);
    }

    public UserModels.GenericStatus deleteUser(String id, String reassign) {
        String path = "users/" + URLEncoder.encode(id, StandardCharsets.UTF_8);
        if (reassign != null && !reassign.isBlank()) {
            path += "?reassign=" + URLEncoder.encode(reassign, StandardCharsets.UTF_8);
        }
        HttpRequest request = baseClient.newRequestBuilder(path)
                .DELETE()
                .build();
        return baseClient.execute(request, UserModels.GenericStatus.class);
    }

    public UserModels.BulkResponse createUsers(UserModels.CreateUserRequest requestPayload) {
        String body = baseClient.toJson(requestPayload);
        HttpRequest request = baseClient.newRequestBuilder("users")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return baseClient.execute(request, UserModels.BulkResponse.class);
    }

    public UserModels.BulkResponse updateUsersBulk(UserModels.UpdateUsersBulkRequest requestPayload) {
        String body = baseClient.toJson(requestPayload);
        HttpRequest request = baseClient.newRequestBuilder("users")
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return baseClient.execute(request, UserModels.BulkResponse.class);
    }

    public UserModels.GenericStatus updateUser(String id, UserModels.UpdateUserRequest requestPayload, Boolean disableActiveTokens, Integer version) {
        String path = "users/" + URLEncoder.encode(id, StandardCharsets.UTF_8);
        StringBuilder query = new StringBuilder();
        if (disableActiveTokens != null) {
            query.append("disableActiveTokens=").append(disableActiveTokens);
        }
        if (version != null) {
            if (query.length() > 0) query.append("&");
            query.append("version=").append(version);
        }
        if (query.length() > 0) {
            path += "?" + query.toString();
        }
        String body = baseClient.toJson(requestPayload);
        HttpRequest request = baseClient.newRequestBuilder(path)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return baseClient.execute(request, UserModels.GenericStatus.class);
    }

    public User getUserByUsername(String username, String photoSize) {
        String path = "users/username/" + URLEncoder.encode(username, StandardCharsets.UTF_8);
        if (photoSize != null && !photoSize.isBlank()) {
            path += "?photoSize=" + URLEncoder.encode(photoSize, StandardCharsets.UTF_8);
        }
        HttpRequest request = baseClient.newRequestBuilder(path)
                .GET()
                .build();
        return baseClient.execute(request, User.class);
    }

    public List<UserModels.UserRightItem> getUserRights(String id, Boolean detailed) {
        String path = "users/" + URLEncoder.encode(id, StandardCharsets.UTF_8) + "/getRights";
        if (detailed != null) {
            path += "?detailed=" + detailed;
        }
        HttpRequest request = baseClient.newRequestBuilder(path)
                .GET()
                .build();
        return List.of(baseClient.execute(request, UserModels.UserRightItem[].class));
    }

    public UserModels.LoginResponse activateUser(Map<String, String> fields, byte[] photoBytes, String photoFilename) {
        String boundary = "Boundary-" + UUID.randomUUID();
        byte[] body = buildMultipartBody(boundary, fields, photoBytes, "photo", photoFilename, "image/octet-stream");
        HttpRequest request = baseClient.newRequestBuilder("users/activate")
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .PUT(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();
        return baseClient.execute(request, UserModels.LoginResponse.class);
    }

    public UserModels.GenericStatus resendActivation(String id, String tenant) {
        String path = "users/" + URLEncoder.encode(id, StandardCharsets.UTF_8) + "/resendActivation";
        if (tenant != null && !tenant.isBlank()) {
            path += "?tenant=" + URLEncoder.encode(tenant, StandardCharsets.UTF_8);
        }
        HttpRequest request = baseClient.newRequestBuilder(path)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return baseClient.execute(request, UserModels.GenericStatus.class);
    }

    public UserModels.UserActivation checkActivationToken() {
        HttpRequest request = baseClient.newRequestBuilder("users/activation")
                .GET()
                .build();
        return baseClient.execute(request, UserModels.UserActivation.class);
    }

    public UserModels.GenericStatus changePassword(String id, UserModels.ChangePasswordRequest requestPayload) {
        String path = "users/" + URLEncoder.encode(id, StandardCharsets.UTF_8) + "/password";
        String body = baseClient.toJson(requestPayload);
        HttpRequest request = baseClient.newRequestBuilder(path)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return baseClient.execute(request, UserModels.GenericStatus.class);
    }

    public UserModels.PasswordStrength checkPasswordStrength(String password, String tenant) {
        String path = "users/password/check";
        if (tenant != null && !tenant.isBlank()) {
            path += "?tenant=" + URLEncoder.encode(tenant, StandardCharsets.UTF_8);
        }
        Map<String, String> payload = Map.of("password", password);
        String body = baseClient.toJson(payload);
        HttpRequest request = baseClient.newRequestBuilder(path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return baseClient.execute(request, UserModels.PasswordStrength.class);
    }

    public UserModels.GenericStatus resetPassword(UserModels.ResetPasswordRequest requestPayload) {
        String body = baseClient.toJson(requestPayload);
        HttpRequest request = baseClient.newRequestBuilder("users/reset")
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return baseClient.execute(request, UserModels.GenericStatus.class);
    }

    public UserModels.GenericStatus resetPasswordUsername(String username, String tenant) {
        String path = "users/reset/username?username=" + URLEncoder.encode(username, StandardCharsets.UTF_8);
        if (tenant != null && !tenant.isBlank()) {
            path += "&tenant=" + URLEncoder.encode(tenant, StandardCharsets.UTF_8);
        }
        HttpRequest request = baseClient.newRequestBuilder(path)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        return baseClient.execute(request, UserModels.GenericStatus.class);
    }

    public UserModels.UserResetToken checkResetToken(String resetToken) {
        String path = "users/reset/check?resetToken=" + URLEncoder.encode(resetToken, StandardCharsets.UTF_8);
        HttpRequest request = baseClient.newRequestBuilder(path)
                .GET()
                .build();
        return baseClient.execute(request, UserModels.UserResetToken.class);
    }

    public UserModels.GenericStatus addUserToGroup(String id, String groupname) {
        String path = "users/" + URLEncoder.encode(id, StandardCharsets.UTF_8) + "/addToGroup?groupname=" + URLEncoder.encode(groupname, StandardCharsets.UTF_8);
        HttpRequest request = baseClient.newRequestBuilder(path)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return baseClient.execute(request, UserModels.GenericStatus.class);
    }

    public UserModels.GenericStatus removeUserFromGroup(String id, String groupname) {
        String path = "users/" + URLEncoder.encode(id, StandardCharsets.UTF_8) + "/removeFromGroup?groupname=" + URLEncoder.encode(groupname, StandardCharsets.UTF_8);
        HttpRequest request = baseClient.newRequestBuilder(path)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return baseClient.execute(request, UserModels.GenericStatus.class);
    }

    public UserModels.UserConfiguration uploadPhoto(String id, byte[] photoBytes, String filename) {
        String path = "users/" + URLEncoder.encode(id, StandardCharsets.UTF_8) + "/upload";
        String boundary = "Boundary-" + UUID.randomUUID();
        byte[] body = buildMultipartBody(boundary, Map.of(), photoBytes, "photo", filename, "image/octet-stream");
        HttpRequest request = baseClient.newRequestBuilder(path)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .PUT(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();
        return baseClient.execute(request, UserModels.UserConfiguration.class);
    }

    public UserModels.GenericStatus deletePhoto(String id) {
        String path = "users/" + URLEncoder.encode(id, StandardCharsets.UTF_8) + "/photo";
        HttpRequest request = baseClient.newRequestBuilder(path)
                .DELETE()
                .build();
        return baseClient.execute(request, UserModels.GenericStatus.class);
    }

    public UserModels.GenericStatus deleteSignature(String id) {
        String path = "users/" + URLEncoder.encode(id, StandardCharsets.UTF_8) + "/signature";
        HttpRequest request = baseClient.newRequestBuilder(path)
                .DELETE()
                .build();
        return baseClient.execute(request, UserModels.GenericStatus.class);
    }

    public UserModels.GenericStatus deleteInitials(String id) {
        String path = "users/" + URLEncoder.encode(id, StandardCharsets.UTF_8) + "/initials";
        HttpRequest request = baseClient.newRequestBuilder(path)
                .DELETE()
                .build();
        return baseClient.execute(request, UserModels.GenericStatus.class);
    }

    public UserModels.GenericStatus addCertificate(String id, UserModels.AddCertificateRequest requestPayload) {
        String path = "users/" + URLEncoder.encode(id, StandardCharsets.UTF_8) + "/certificate";
        String body = baseClient.toJson(requestPayload);
        HttpRequest request = baseClient.newRequestBuilder(path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return baseClient.execute(request, UserModels.GenericStatus.class);
    }

    public UserModels.GenericStatus removeCertificate(String id, String certificateId) {
        String path = "users/" + URLEncoder.encode(id, StandardCharsets.UTF_8) + "/certificate/" + URLEncoder.encode(certificateId, StandardCharsets.UTF_8);
        HttpRequest request = baseClient.newRequestBuilder(path)
                .DELETE()
                .build();
        return baseClient.execute(request, UserModels.GenericStatus.class);
    }

    public UserModels.GenericStatus registerUser(UserModels.RegisterUserRequest requestPayload) {
        String body = baseClient.toJson(requestPayload);
        HttpRequest request = baseClient.newRequestBuilder("users/register")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return baseClient.execute(request, UserModels.GenericStatus.class);
    }

    private byte[] buildMultipartBody(String boundary, Map<String, String> fields, byte[] fileBytes, String fileFieldName, String filename, String fileContentType) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                if (entry.getValue() != null) {
                    baos.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
                    baos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                    baos.write((entry.getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
                }
            }
            if (fileBytes != null && fileFieldName != null) {
                baos.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
                baos.write(("Content-Disposition: form-data; name=\"" + fileFieldName + "\"; filename=\"" + filename + "\"\r\n").getBytes(StandardCharsets.UTF_8));
                baos.write(("Content-Type: " + fileContentType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                baos.write(fileBytes);
                baos.write("\r\n".getBytes(StandardCharsets.UTF_8));
            }
            baos.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }
}
