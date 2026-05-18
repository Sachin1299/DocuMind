package com.sachin.documind.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Service responsible for automatically starting Qdrant when the Spring Boot application starts.
 * It reads the path to the executable from the configuration and checks if the process is already running.
 */
@Service
public class QdrantStartupService {

    private static final Logger logger = LoggerFactory.getLogger(QdrantStartupService.class);

    @Value("${qdrant.executable.path}")
    private String qdrantExecutablePath;

    /**
     * Listens for the ApplicationReadyEvent to start Qdrant once the Spring context is fully initialized.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startQdrant() {
        // Ensure this only attempts to run on Windows as requested
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            if (isQdrantRunning()) {
                logger.info("Qdrant is already running. Skipping startup.");
                return;
            }

            logger.info("Qdrant is not running. Attempting to start from: {}", qdrantExecutablePath);
            try {
                File executableFile = new File(qdrantExecutablePath);
                if (!executableFile.exists()) {
                    logger.error("Qdrant executable not found at specified path: {}. Please check your application.properties.", qdrantExecutablePath);
                    return;
                }

                // Proper production-style implementation using Java ProcessBuilder
                ProcessBuilder processBuilder = new ProcessBuilder(qdrantExecutablePath);
                // Set the working directory to the executable's folder
                processBuilder.directory(executableFile.getParentFile());
                // Start the process detached
                processBuilder.start();

                logger.info("Qdrant process started successfully.");
            } catch (IOException e) {
                logger.error("Failed to start Qdrant process.", e);
            } catch (Exception e) {
                logger.error("An unexpected error occurred while starting Qdrant.", e);
            }
        } else {
            logger.info("OS is not Windows. Qdrant auto-startup skips execution.");
        }
    }

    /**
     * Checks if qdrant.exe is currently running in the system processes.
     *
     * @return true if qdrant.exe is running, false otherwise.
     */
    private boolean isQdrantRunning() {
        return ProcessHandle.allProcesses()
                .anyMatch(process -> {
                    Optional<String> command = process.info().command();
                    return command.isPresent() && command.get().toLowerCase().contains("qdrant.exe");
                });
    }
}
