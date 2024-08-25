package com.kuro.kurolineuserms.repositories;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileRepository {
    String upload(MultipartFile file, String newFileName) throws IOException;
//    String get(String userId);
}
