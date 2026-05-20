package com.circularo.cli.cli;

import com.circularo.cli.api.ReportsApiClient;
import com.circularo.cli.model.WorkflowReport;

import java.util.List;

/**
 * AOT-friendly stub for ReportsApiClient.
 */
class StubReportsApiClient extends ReportsApiClient {
    private List<WorkflowReport> stubReports;
    private RuntimeException exceptionToThrow;

    public StubReportsApiClient() {
        super(null);
    }

    public void setStubReports(List<WorkflowReport> reports) {
        this.stubReports = reports;
    }

    public void setException(RuntimeException ex) {
        this.exceptionToThrow = ex;
    }

    @Override
    public List<WorkflowReport> searchReports(String query) {
        if (exceptionToThrow != null) {
            throw exceptionToThrow;
        }
        return stubReports;
    }
}
