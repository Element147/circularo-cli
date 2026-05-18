package com.circularo.cli;

import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CircularoCliApplication implements CommandLineRunner, ExitCodeGenerator {

    private final IFactory factory;
    private final RootCmd rootCmd;
    private int exitCode;

    public CircularoCliApplication(IFactory factory, RootCmd rootCmd) {
        this.factory = factory;
        this.rootCmd = rootCmd;
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(CircularoCliApplication.class, args)));
    }

    @Override
    public void run(String... args) {
        exitCode = new CommandLine(rootCmd, factory)
                .setExecutionExceptionHandler(new com.circularo.cli.cli.CircularoExecutionExceptionHandler())
                .execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
