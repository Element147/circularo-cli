package com.circularo.cli.cli;

import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.MarkdownFormatter;
import com.circularo.cli.format.TableFormatter;
import com.circularo.cli.model.User;
import com.circularo.cli.model.UserModels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsersCmdTest {

    private StubUsersApiClient apiClient;
    private TableFormatter tableFormatter;
    private JsonFormatter jsonFormatter;
    private MarkdownFormatter markdownFormatter;

    @BeforeEach
    void setUp() {
        apiClient = new StubUsersApiClient();
        tableFormatter = new TableFormatter();
        jsonFormatter = new JsonFormatter();
        markdownFormatter = new MarkdownFormatter();
    }

    @Test
    void run_searchCmd_success() {
        List<User> mockUsers = List.of(new User("1", "Mary Griffin", "mary@example.com", "mary", null, null, "admin", "active"));
        apiClient.setStubUsers(mockUsers);

        StringWriter out = new StringWriter();
        UsersSearchCmd cmdObj = new UsersSearchCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-q", "mary", "-f", "table");

        assertEquals(0, exitCode);
        assertTrue(out.toString().contains("Mary Griffin"));
    }

    @Test
    void run_getCmd_success() {
        User mockUser = new User("1", "Mary Griffin", "mary@example.com", "mary", null, null, "admin", "active");
        apiClient.setStubUser(mockUser);

        StringWriter out = new StringWriter();
        UsersGetCmd cmdObj = new UsersGetCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("1", "-f", "table");

        assertEquals(0, exitCode);
        assertEquals("1", apiClient.lastId);
        assertTrue(out.toString().contains("Mary Griffin"));
    }

    @Test
    void run_deleteCmd_success() {
        apiClient.setStubGenericStatus(new UserModels.GenericStatus("deleted"));

        StringWriter out = new StringWriter();
        UsersDeleteCmd cmdObj = new UsersDeleteCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("1", "--reassign", "user2", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("1", apiClient.lastId);
        assertEquals("user2", apiClient.lastReassign);
        assertTrue(out.toString().contains("\"status\" : \"deleted\""));
    }

    @Test
    void run_createCmd_success(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("create.json");
        Files.writeString(file, "{\"users\": [], \"sendMail\": true}");
        apiClient.setStubBulkResponse(new UserModels.BulkResponse(0, false, List.of()));

        StringWriter out = new StringWriter();
        UsersCreateCmd cmdObj = new UsersCreateCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("--file", file.toString(), "-f", "json");

        assertEquals(0, exitCode);
        assertTrue(apiClient.lastCreateRequest.sendMail());
        assertTrue(out.toString().contains("\"count\" : 0"));
    }

    @Test
    void run_updateCmd_success(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("update.json");
        Files.writeString(file, "{\"ids\": [\"1\"], \"update\": {}}");
        apiClient.setStubBulkResponse(new UserModels.BulkResponse(1, false, List.of()));

        StringWriter out = new StringWriter();
        UsersUpdateCmd cmdObj = new UsersUpdateCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("--file", file.toString(), "-f", "json");

        assertEquals(0, exitCode);
        assertEquals(List.of("1"), apiClient.lastUpdateBulkRequest.ids());
        assertTrue(out.toString().contains("\"count\" : 1"));
    }

    @Test
    void run_updateSingleCmd_success(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("update-single.json");
        Files.writeString(file, "{\"mail\": \"new@example.com\"}");
        apiClient.setStubGenericStatus(new UserModels.GenericStatus("updated"));

        StringWriter out = new StringWriter();
        UsersUpdateSingleCmd cmdObj = new UsersUpdateSingleCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("1", "--file", file.toString(), "--disable-active-tokens", "--version", "3", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("1", apiClient.lastId);
        assertEquals("new@example.com", apiClient.lastUpdateRequest.mail());
        assertEquals(true, apiClient.lastDisableActiveTokens);
        assertEquals(3, apiClient.lastVersion);
        assertTrue(out.toString().contains("\"status\" : \"updated\""));
    }

    @Test
    void run_getByUsernameCmd_success() {
        User mockUser = new User("1", "Mary Griffin", "mary@example.com", "mary", null, null, "admin", "active");
        apiClient.setStubUser(mockUser);

        StringWriter out = new StringWriter();
        UsersGetByUsernameCmd cmdObj = new UsersGetByUsernameCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("mary", "--photo-size", "large", "-f", "table");

        assertEquals(0, exitCode);
        assertEquals("mary", apiClient.lastUsername);
        assertEquals("large", apiClient.lastPhotoSize);
        assertTrue(out.toString().contains("Mary Griffin"));
    }

    @Test
    void run_rightsCmd_success() {
        apiClient.setStubUserRights(List.of(new UserModels.UserRightItem(List.of("read"), "action", true, "class", "definition", "desc", "group", "name", "workflow")));

        StringWriter out = new StringWriter();
        UsersRightsCmd cmdObj = new UsersRightsCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("1", "--detailed", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("1", apiClient.lastId);
        assertEquals(true, apiClient.lastDetailed);
        assertTrue(out.toString().contains("\"actionname\" : \"action\""));
    }

    @Test
    void run_activateCmd_success(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("photo.png");
        Files.writeString(file, "dummy content");
        apiClient.setStubLoginResponse(new UserModels.LoginResponse("token123", null, List.of()));

        StringWriter out = new StringWriter();
        UsersActivateCmd cmdObj = new UsersActivateCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("--password", "pass123", "--fullname", "Mary", "--file", file.toString(), "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("pass123", apiClient.lastFields.get("password"));
        assertEquals("Mary", apiClient.lastFields.get("fullname"));
        assertEquals("photo.png", apiClient.lastPhotoFilename);
        assertTrue(out.toString().contains("\"token\" : \"token123\""));
    }

    @Test
    void run_resendActivationCmd_success() {
        apiClient.setStubGenericStatus(new UserModels.GenericStatus("sent"));

        StringWriter out = new StringWriter();
        UsersResendActivationCmd cmdObj = new UsersResendActivationCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("1", "--tenant", "tenantA", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("1", apiClient.lastId);
        assertEquals("tenantA", apiClient.lastTenant);
        assertTrue(out.toString().contains("\"status\" : \"sent\""));
    }

    @Test
    void run_checkActivationCmd_success() {
        apiClient.setStubUserActivation(new UserModels.UserActivation(true, null));

        StringWriter out = new StringWriter();
        UsersCheckActivationCmd cmdObj = new UsersCheckActivationCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-f", "json");

        assertEquals(0, exitCode);
        assertTrue(out.toString().contains("\"accountActivated\" : true"));
    }

    @Test
    void run_changePasswordCmd_success() {
        apiClient.setStubGenericStatus(new UserModels.GenericStatus("changed"));

        StringWriter out = new StringWriter();
        UsersChangePasswordCmd cmdObj = new UsersChangePasswordCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("1", "--current", "oldpass", "--new", "newpass", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("1", apiClient.lastId);
        assertEquals("newpass", apiClient.lastChangePasswordRequest.password());
        assertEquals("oldpass", apiClient.lastChangePasswordRequest.currentPassword());
        assertTrue(out.toString().contains("\"status\" : \"changed\""));
    }

    @Test
    void run_checkPasswordCmd_success() {
        apiClient.setStubPasswordStrength(new UserModels.PasswordStrength(4, List.of(), "warning"));

        StringWriter out = new StringWriter();
        UsersCheckPasswordCmd cmdObj = new UsersCheckPasswordCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("--password", "pass123", "--tenant", "tenantA", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("pass123", apiClient.lastPassword);
        assertEquals("tenantA", apiClient.lastTenant);
        assertTrue(out.toString().contains("\"score\" : 4"));
    }

    @Test
    void run_resetPasswordCmd_success() {
        apiClient.setStubGenericStatus(new UserModels.GenericStatus("reset"));

        StringWriter out = new StringWriter();
        UsersResetPasswordCmd cmdObj = new UsersResetPasswordCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("--username", "mary", "--code", "123", "--new-password", "newpass", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("mary", apiClient.lastResetPasswordRequest.username());
        assertEquals("123", apiClient.lastResetPasswordRequest.code());
        assertEquals("newpass", apiClient.lastResetPasswordRequest.password());
        assertTrue(out.toString().contains("\"status\" : \"reset\""));
    }

    @Test
    void run_resetPasswordUsernameCmd_success() {
        apiClient.setStubGenericStatus(new UserModels.GenericStatus("initiated"));

        StringWriter out = new StringWriter();
        UsersResetPasswordUsernameCmd cmdObj = new UsersResetPasswordUsernameCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("mary", "--tenant", "tenantA", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("mary", apiClient.lastUsername);
        assertEquals("tenantA", apiClient.lastTenant);
        assertTrue(out.toString().contains("\"status\" : \"initiated\""));
    }

    @Test
    void run_checkResetTokenCmd_success() {
        apiClient.setStubUserResetToken(new UserModels.UserResetToken(true, "mary@example.com", "expires"));

        StringWriter out = new StringWriter();
        UsersCheckResetTokenCmd cmdObj = new UsersCheckResetTokenCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("--token", "token123", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("token123", apiClient.lastResetToken);
        assertTrue(out.toString().contains("\"valid\" : true"));
    }

    @Test
    void run_addGroupCmd_success() {
        apiClient.setStubGenericStatus(new UserModels.GenericStatus("added"));

        StringWriter out = new StringWriter();
        UsersAddGroupCmd cmdObj = new UsersAddGroupCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("1", "--group", "groupA", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("1", apiClient.lastId);
        assertEquals("groupA", apiClient.lastGroupname);
        assertTrue(out.toString().contains("\"status\" : \"added\""));
    }

    @Test
    void run_removeGroupCmd_success() {
        apiClient.setStubGenericStatus(new UserModels.GenericStatus("removed"));

        StringWriter out = new StringWriter();
        UsersRemoveGroupCmd cmdObj = new UsersRemoveGroupCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("1", "--group", "groupA", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("1", apiClient.lastId);
        assertEquals("groupA", apiClient.lastGroupname);
        assertTrue(out.toString().contains("\"status\" : \"removed\""));
    }

    @Test
    void run_uploadPhotoCmd_success(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("photo.png");
        Files.writeString(file, "dummy image content");
        apiClient.setStubUserConfiguration(new UserModels.UserConfiguration(null, null, "en", null, null, null, null, null, null));

        StringWriter out = new StringWriter();
        UsersUploadPhotoCmd cmdObj = new UsersUploadPhotoCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("1", "--file", file.toString(), "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("1", apiClient.lastId);
        assertEquals("photo.png", apiClient.lastPhotoFilename);
        assertTrue(out.toString().contains("\"language\" : \"en\""));
    }

    @Test
    void run_deletePhotoCmd_success() {
        apiClient.setStubGenericStatus(new UserModels.GenericStatus("photo-deleted"));

        StringWriter out = new StringWriter();
        UsersDeletePhotoCmd cmdObj = new UsersDeletePhotoCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("1", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("1", apiClient.lastId);
        assertTrue(out.toString().contains("\"status\" : \"photo-deleted\""));
    }

    @Test
    void run_deleteSignatureCmd_success() {
        apiClient.setStubGenericStatus(new UserModels.GenericStatus("sig-deleted"));

        StringWriter out = new StringWriter();
        UsersDeleteSignatureCmd cmdObj = new UsersDeleteSignatureCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("1", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("1", apiClient.lastId);
        assertTrue(out.toString().contains("\"status\" : \"sig-deleted\""));
    }

    @Test
    void run_deleteInitialsCmd_success() {
        apiClient.setStubGenericStatus(new UserModels.GenericStatus("initials-deleted"));

        StringWriter out = new StringWriter();
        UsersDeleteInitialsCmd cmdObj = new UsersDeleteInitialsCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("1", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("1", apiClient.lastId);
        assertTrue(out.toString().contains("\"status\" : \"initials-deleted\""));
    }

    @Test
    void run_addCertificateCmd_success(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("cert.pem");
        Files.writeString(file, "dummy cert content");
        apiClient.setStubGenericStatus(new UserModels.GenericStatus("cert-added"));

        StringWriter out = new StringWriter();
        UsersAddCertificateCmd cmdObj = new UsersAddCertificateCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("1", "--name", "MyCert", "--type", "personal", "--file", file.toString(), "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("1", apiClient.lastId);
        assertEquals("MyCert", apiClient.lastAddCertificateRequest.name());
        assertEquals("personal", apiClient.lastAddCertificateRequest.type());
        assertTrue(out.toString().contains("\"status\" : \"cert-added\""));
    }

    @Test
    void run_removeCertificateCmd_success() {
        apiClient.setStubGenericStatus(new UserModels.GenericStatus("cert-removed"));

        StringWriter out = new StringWriter();
        UsersRemoveCertificateCmd cmdObj = new UsersRemoveCertificateCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("1", "--cert-id", "cert123", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("1", apiClient.lastId);
        assertEquals("cert123", apiClient.lastCertificateId);
        assertTrue(out.toString().contains("\"status\" : \"cert-removed\""));
    }

    @Test
    void run_registerCmd_success() {
        apiClient.setStubGenericStatus(new UserModels.GenericStatus("registered"));

        StringWriter out = new StringWriter();
        UsersRegisterCmd cmdObj = new UsersRegisterCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("--name", "mary", "--email", "mary@example.com", "--fullname", "Mary", "-f", "json");

        assertEquals(0, exitCode);
        assertEquals("mary", apiClient.lastRegisterUserRequest.name());
        assertEquals("mary@example.com", apiClient.lastRegisterUserRequest.mail());
        assertEquals("Mary", apiClient.lastRegisterUserRequest.fullname());
        assertTrue(out.toString().contains("\"status\" : \"registered\""));
    }

    @Test
    void run_usersCmd_printsUsage() {
        CommandLine.IFactory factory = new CommandLine.IFactory() {
            @Override
            public <K> K create(Class<K> cls) throws Exception {
                if (cls == UsersSearchCmd.class) return (K) new UsersSearchCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersGetCmd.class) return (K) new UsersGetCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersDeleteCmd.class) return (K) new UsersDeleteCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersCreateCmd.class) return (K) new UsersCreateCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersUpdateCmd.class) return (K) new UsersUpdateCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersUpdateSingleCmd.class) return (K) new UsersUpdateSingleCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersGetByUsernameCmd.class) return (K) new UsersGetByUsernameCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersRightsCmd.class) return (K) new UsersRightsCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersActivateCmd.class) return (K) new UsersActivateCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersResendActivationCmd.class) return (K) new UsersResendActivationCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersCheckActivationCmd.class) return (K) new UsersCheckActivationCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersChangePasswordCmd.class) return (K) new UsersChangePasswordCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersCheckPasswordCmd.class) return (K) new UsersCheckPasswordCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersResetPasswordCmd.class) return (K) new UsersResetPasswordCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersResetPasswordUsernameCmd.class) return (K) new UsersResetPasswordUsernameCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersCheckResetTokenCmd.class) return (K) new UsersCheckResetTokenCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersAddGroupCmd.class) return (K) new UsersAddGroupCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersRemoveGroupCmd.class) return (K) new UsersRemoveGroupCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersUploadPhotoCmd.class) return (K) new UsersUploadPhotoCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersDeletePhotoCmd.class) return (K) new UsersDeletePhotoCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersDeleteSignatureCmd.class) return (K) new UsersDeleteSignatureCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersDeleteInitialsCmd.class) return (K) new UsersDeleteInitialsCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersAddCertificateCmd.class) return (K) new UsersAddCertificateCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersRemoveCertificateCmd.class) return (K) new UsersRemoveCertificateCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                if (cls == UsersRegisterCmd.class) return (K) new UsersRegisterCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
                return CommandLine.defaultFactory().create(cls);
            }
        };

        StringWriter out = new StringWriter();
        UsersCmd cmdObj = new UsersCmd();
        CommandLine cmd = new CommandLine(cmdObj, factory);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute();

        assertEquals(0, exitCode);
        assertTrue(out.toString().contains("Manage, search, and modify Circularo user accounts."));
    }
}
