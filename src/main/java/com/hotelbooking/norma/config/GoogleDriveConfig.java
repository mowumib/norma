package com.hotelbooking.norma.config;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Configuration
public class GoogleDriveConfig {

    private static final String APPLICATION_NAME = "HotelPhotoUploader"; // Your application name
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String SERVICE_ACCOUNT_KEY_PATH = "credentials/norma-462118-132ce6db6c1d.json"; // Update this with your actual JSON file name

    @Bean
    Drive googleDriveService() throws IOException, GeneralSecurityException {
        HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        // Load service account key from resources
        InputStream in = new ClassPathResource(SERVICE_ACCOUNT_KEY_PATH).getInputStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
        .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE)); // Scope for file operations
        HttpCredentialsAdapter credentialInitializer = new HttpCredentialsAdapter(credentials);

        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentialInitializer)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }
}
