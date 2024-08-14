package com.kuro.kurolineuserms.repositories;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface FileUploader {
    void upload(MultipartFile file, String newFileName) throws IOException;
}
