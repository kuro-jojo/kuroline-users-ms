package com.kuro.kurolineuserms.services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.kuro.kurolineuserms.repositories.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService implements FileRepository {
    private final static String UPLOAD_PATH = "users-ms/profiles/";

    @Override
    public String upload(MultipartFile file, String userID) throws IOException {
        Blob blob = StorageClient.getInstance().bucket().create(UPLOAD_PATH + userID, file.getInputStream(), file.getContentType());
        String gcsUrl = blob.getMediaLink();

        return gcsUrl.replace("https://storage.googleapis.com/download/storage/v1", "https://firebasestorage.googleapis.com/v0");
    }
}
