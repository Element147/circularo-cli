# 0002 - CLI Application Skeleton

## Triggering Prompt
> Act as an Expert Java Architect. I am taking a strict hiring exercise to build a CLI search utility for the Circularo REST API. The evaluation focuses on clear separation of concerns (CLI, API, Query, Format, Config layers), deterministic output, clean architecture over feature bloat, and demonstrating an intentional "agentic workflow".
> 
> I have already initialized a Spring Boot 4.x project using Gradle (Groovy DSL) with Java 25 and GraalVM Native Support. 
> 
> Please review my workspace and execute the following tasks step-by-step. Do not duplicate existing files, but update them if necessary to meet these requirements:
> 
> Task 1: Fix Dependencies and Configuration
> 1. Check build.gradle. Ensure the following dependencies are present for Picocli integration:
>    - implementation 'info.picocli:picocli-spring-boot-starter:4.7.5'
>    - annotationProcessor 'info.picocli:picocli-codegen:4.7.5'
> 2. Open/Create src/main/resources/application.properties and add: spring.main.web-application-type=none.
> 
> Task 2: Create the CLI Application Skeleton
> 1. Locate or create the main entry point class. It must:
>    - Be annotated with @SpringBootApplication
>    - Implement CommandLineRunner and ExitCodeGenerator
>    - Use Constructor Injection to wire info.picocli.CommandLine.IFactory and a base RootCmd class.
>    - Execute the Picocli command in the run() method and terminate cleanly via System.exit(SpringApplication.exit(context)).
> 2. Create src/main/java/com/circularo/cli/RootCmd.java annotated with @Component and @Command.
> 
> Task 3: Setup Documentation (Architecture Decision Records)
> Create docs/decisions.md documenting Agentic Workflow, Tech Stack, and Dual Distribution Strategy.
> 
> Task 4: Create README.md
> Update README with commands for JAR and GraalVM builds.
> 
> Task 5: GitHub Actions CI/CD Pipeline
> Create .github/workflows/ci.yml with Java 25 GraalVM setup and Gradle build/nativeCompile.

## Plan & Steps
1. Add Picocli dependencies to `build.gradle`.
2. Configure `application.properties` with `spring.main.web-application-type=none`.
3. Move `CircularocliApplication.java` to `com.circularo.cli.CircularoCliApplication.java` and implement the CLI base runner pattern.
4. Create `RootCmd.java` in `com.circularo.cli`.
5. Create `docs/decisions.md` to document architecture decisions.
6. Overwrite the root `README.md` with updated content.
7. Create the CI/CD pipeline at `.github/workflows/ci.yml`.
8. Execute verification tests locally (`./gradlew test` and `./gradlew nativeTest`).

## Architectural Decisions
- Moved the root package to `com.circularo.cli` for better alignment with the domain.
- Configured Picocli Spring Boot starter for dependency injection inside CLI commands.
- Configured native GraalVM workflow inside GitHub Actions to ensure sub-millisecond cold starts for downstream users.

## Expected Outcome
A cleanly structured CLI application that immediately terminates after returning the executed command's status code. It will be fully verifiable via standard Spring Boot testing and ready for GraalVM compilation without reflection/AOT issues.
