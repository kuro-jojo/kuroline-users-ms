package com.kuro.kurolineuserms.services;

import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import com.kuro.kurolineuserms.repositories.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service for handling file operations.
 */
@Service
public class FileService implements FileRepository {
    private final static String UPLOAD_PATH = "users-ms/profiles/";

    /**
     * Uploads a file to Google Cloud Storage and returns the Firebase Storage URL.
     *
     * @param file   the file to upload
     * @param userID the user ID to associate with the file
     * @return the Firebase Storage URL of the uploaded file
     * @throws IOException if an I/O error occurs during file upload
     */
    @Override
    public String upload(MultipartFile file, String userID) throws IOException {
        try {
            Blob blob = StorageClient.getInstance().bucket().create(UPLOAD_PATH + userID, file.getInputStream(), file.getContentType());
            return convertToFirebaseUrl(blob.getMediaLink());
        } catch (IOException e) {
            // Log the exception (logging framework should be used in real scenarios)
            throw new IOException("Failed to upload file", e);
        }
    }

    /**
     * Converts the Google Cloud Storage URL to a Firebase Storage URL.
     *
     * @param gcsUrl the Google Cloud Storage URL
     * @return the Firebase Storage URL
     */
    private String convertToFirebaseUrl(String gcsUrl) {
        return gcsUrl.replace("https://storage.googleapis.com/download/storage/v1", "https://firebasestorage.googleapis.com/v0");
    }
}