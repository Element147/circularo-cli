package com.circularo.cli.cli.users;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * Parent command group for all user-related subcommands.
 */
@Component
@Command(
        name = "users",
        mixinStandardHelpOptions = true,
        header = "Manage, search, and modify Circularo user accounts.",
        description = "Provides access to all user management operations, including CRUD, authentication, groups, passwords, and media.",
        subcommands = {
                UsersSearchCmd.class,
                UsersGetCmd.class,
                UsersDeleteCmd.class,
                UsersCreateCmd.class,
                UsersUpdateCmd.class,
                UsersUpdateSingleCmd.class,
                UsersGetByUsernameCmd.class,
                UsersRightsCmd.class,
                UsersActivateCmd.class,
                UsersResendActivationCmd.class,
                UsersCheckActivationCmd.class,
                UsersChangePasswordCmd.class,
                UsersCheckPasswordCmd.class,
                UsersResetPasswordCmd.class,
                UsersResetPasswordUsernameCmd.class,
                UsersCheckResetTokenCmd.class,
                UsersAddGroupCmd.class,
                UsersRemoveGroupCmd.class,
                UsersUploadPhotoCmd.class,
                UsersDeletePhotoCmd.class,
                UsersDeleteSignatureCmd.class,
                UsersDeleteInitialsCmd.class,
                UsersAddCertificateCmd.class,
                UsersRemoveCertificateCmd.class,
                UsersRegisterCmd.class
        }
)
public class UsersCmd implements Runnable {

    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}
