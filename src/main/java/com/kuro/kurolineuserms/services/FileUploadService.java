package com.kuro.kurolineuserms.services;

import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.kuro.kurolineuserms.repositories.FileUploader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileUploadService implements FileUploader {
    private final static String UPLOAD_PATH = "users-ms/";
    @Override
    public void upload(MultipartFile file, String newFileName) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();
        bucket.create(UPLOAD_PATH + newFileName, file.getInputStream());
    }
}
