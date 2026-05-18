package com.circularo.cli.cli;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * Picocli sub-command for document search operations.
 * Registered as a Spring {@link Component} so that Picocli's Spring factory
 * can inject dependencies when they are added in future iterations.
 */
@Component
@Command(
        name = "documents",
        description = "Search and list Circularo documents",
        mixinStandardHelpOptions = true
)
public class DocumentsCmd implements Runnable {

    @Override
    public void run() {
        System.out.println("Executing search... (API integration pending)");
    }
}
