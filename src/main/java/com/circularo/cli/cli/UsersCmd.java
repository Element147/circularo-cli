package com.circularo.cli.cli;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * Picocli sub-command for user search operations.
 * Registered as a Spring {@link Component} so that Picocli's Spring factory
 * can inject dependencies when they are added in future iterations.
 */
@Component
@Command(
        name = "users",
        description = "Search and list Circularo users",
        mixinStandardHelpOptions = true
)
public class UsersCmd implements Runnable {

    @Override
    public void run() {
        System.out.println("Executing search... (API integration pending)");
    }
}
