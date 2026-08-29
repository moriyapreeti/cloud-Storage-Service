package com.cloudstorage.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface SupabaseStorageService {

    void uploadFile(
            String filePath,
            MultipartFile file) throws IOException;

    void deleteFile(
            String filePath);

    String createSignedUrl(
            String filePath,
            int expiresInSeconds);
  
}