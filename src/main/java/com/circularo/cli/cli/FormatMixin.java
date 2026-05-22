package com.circularo.cli.cli;

import picocli.CommandLine.Option;

/**
 * Picocli mixin that centralizes the {@code --format} option definition.
 * Every CLI command should include this via {@code @Mixin} instead of
 * declaring its own format option field.
 *
 * <p>Supported formats: {@code table} (default), {@code json}, {@code md}.</p>
 */
public class FormatMixin {

    @Option(names = {"-f", "--format"}, description = "Output format (table, json, md)", defaultValue = "table")
    private String format;

    public String getFormat() {
        return format;
    }
}
