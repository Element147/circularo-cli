package com.circularo.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class UserModels {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GenericStatus(
        @JsonProperty("status") String status
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BulkResponse(
        @JsonProperty("count") int count,
        @JsonProperty("errors") boolean errors,
        @JsonProperty("items") List<BulkResponseItem> items
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BulkResponseItem(
        @JsonProperty("id") String id,
        @JsonProperty("index") String index,
        @JsonProperty("message") String message,
        @JsonProperty("payload") Map<String, Object> payload,
        @JsonProperty("status") Integer status
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UserRightItem(
        @JsonProperty("acls") List<String> acls,
        @JsonProperty("actionname") String actionname,
        @JsonProperty("allowedForOrganization") Boolean allowedForOrganization,
        @JsonProperty("class") String className,
        @JsonProperty("definitionname") String definitionname,
        @JsonProperty("description") String description,
        @JsonProperty("group") String group,
        @JsonProperty("name") String name,
        @JsonProperty("workflowname") String workflowname
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PasswordStrength(
        @JsonProperty("score") int score,
        @JsonProperty("suggestions") List<String> suggestions,
        @JsonProperty("warning") String warning
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UserActivation(
        @JsonProperty("accountActivated") Boolean accountActivated,
        @JsonProperty("user") User user
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UserResetToken(
        @JsonProperty("valid") Boolean valid,
        @JsonProperty("email") String email,
        @JsonProperty("expires") String expires
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LoginResponse(
        @JsonProperty("token") String token,
        @JsonProperty("user") User user,
        @JsonProperty("rights") List<String> rights
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UserConfiguration(
        @JsonProperty("delegation") Map<String, Object> delegation,
        @JsonProperty("initials") List<Map<String, Object>> initials,
        @JsonProperty("language") String language,
        @JsonProperty("navigationOpen") Boolean navigationOpen,
        @JsonProperty("notifications") Map<String, Object> notifications,
        @JsonProperty("photo") Map<String, Object> photo,
        @JsonProperty("scheduler") Boolean scheduler,
        @JsonProperty("signature") List<Map<String, Object>> signature,
        @JsonProperty("view") String view
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CreateUserRequest(
        @JsonProperty("users") List<CreateUserData> users,
        @JsonProperty("sendMail") Boolean sendMail,
        @JsonProperty("edition") String edition,
        @JsonProperty("createdOnBehalfOf") String createdOnBehalfOf
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CreateUserData(
        @JsonProperty("mail") String mail,
        @JsonProperty("fullname") String fullname,
        @JsonProperty("name") String name,
        @JsonProperty("phone") String phone,
        @JsonProperty("role") String role,
        @JsonProperty("status") String status,
        @JsonProperty("group") List<String> group
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UpdateUserRequest(
        @JsonProperty("mail") String mail,
        @JsonProperty("fullname") String fullname,
        @JsonProperty("name") String name,
        @JsonProperty("phone") String phone,
        @JsonProperty("role") String role,
        @JsonProperty("status") String status,
        @JsonProperty("group") List<String> group
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UpdateUsersBulkRequest(
        @JsonProperty("users") List<String> users,
        @JsonProperty("data") Map<String, Object> data
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ChangePasswordRequest(
        @JsonProperty("currentPassword") String currentPassword,
        @JsonProperty("newPassword") String newPassword
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ResetPasswordRequest(
        @JsonProperty("resetToken") String resetToken,
        @JsonProperty("password") String password
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AddCertificateRequest(
        @JsonProperty("file") String file,
        @JsonProperty("name") String name,
        @JsonProperty("type") String type
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RegisterUserRequest(
        @JsonProperty("mail") String mail,
        @JsonProperty("fullname") String fullname,
        @JsonProperty("name") String name,
        @JsonProperty("phone") String phone,
        @JsonProperty("company") String company,
        @JsonProperty("edition") String edition,
        @JsonProperty("language") String language
    ) {}
}
