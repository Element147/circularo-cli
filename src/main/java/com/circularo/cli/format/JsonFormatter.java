package com.circularo.cli.format;

import com.circularo.cli.model.User;
import com.circularo.cli.model.Document;
import com.circularo.cli.model.Workflow;
import com.circularo.cli.model.WorkflowReport;
import com.circularo.cli.model.WorkflowReportItem;
import com.circularo.cli.model.UserModels;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Deterministic JSON output formatter.
 * Produces structured, machine-readable, pretty-printed representations of the domain models.
 */
@Component
@RegisterReflectionForBinding({User.class, Document.class, Workflow.class, WorkflowReport.class, WorkflowReportItem.class})
public class JsonFormatter implements OutputFormatter {

    private final ObjectMapper objectMapper;

    public JsonFormatter() {
        this.objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public String formatUsers(List<User> users) {
        try {
            return objectMapper.writeValueAsString(users);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize users to JSON", e);
        }
    }

    @Override
    public String formatDocuments(List<Document> documents) {
        try {
            return objectMapper.writeValueAsString(documents);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize documents to JSON", e);
        }
    }

    @Override
    public String formatReports(List<WorkflowReport> reports) {
        return formatReports(reports, false);
    }

    @Override
    public String formatReports(List<WorkflowReport> reports, boolean verbose) {
        try {
            return objectMapper.writeValueAsString(reports);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize reports to JSON", e);
        }
    }

    @Override
    public String formatTransactions(List<WorkflowReport> transactions) {
        return formatTransactions(transactions, false);
    }

    @Override
    public String formatTransactions(List<WorkflowReport> transactions, boolean verbose) {
        try {
            return objectMapper.writeValueAsString(transactions);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize transactions to JSON", e);
        }
    }

    @Override
    public String formatGenericStatus(UserModels.GenericStatus status) {
        try {
            return objectMapper.writeValueAsString(status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize GenericStatus to JSON", e);
        }
    }

    @Override
    public String formatBulkResponse(UserModels.BulkResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize BulkResponse to JSON", e);
        }
    }

    @Override
    public String formatUserRights(List<UserModels.UserRightItem> rights) {
        try {
            return objectMapper.writeValueAsString(rights);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize UserRights to JSON", e);
        }
    }

    @Override
    public String formatUserActivation(UserModels.UserActivation activation) {
        try {
            return objectMapper.writeValueAsString(activation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize UserActivation to JSON", e);
        }
    }

    @Override
    public String formatUserResetToken(UserModels.UserResetToken resetToken) {
        try {
            return objectMapper.writeValueAsString(resetToken);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize UserResetToken to JSON", e);
        }
    }

    @Override
    public String formatLoginResponse(UserModels.LoginResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize LoginResponse to JSON", e);
        }
    }

    @Override
    public String formatUserConfiguration(UserModels.UserConfiguration config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize UserConfiguration to JSON", e);
        }
    }

    @Override
    public String formatPasswordStrength(UserModels.PasswordStrength strength) {
        try {
            return objectMapper.writeValueAsString(strength);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize PasswordStrength to JSON", e);
        }
    }
}
