package com.cloudstorage.service;

import org.springframework.web.multipart.MultipartFile;

import com.cloudstorage.dto.response.FileResponse;


import java.util.List;

import com.cloudstorage.dto.response.TrashFileResponse;

public interface FileService {

    String uploadFile(
            MultipartFile file,
            String email);
    
    FileResponse getFile(
            Long fileId,
            String email);

    String deleteFile(
            Long fileId,
            String email);
    
    List<TrashFileResponse> getTrash(
            String email);
    
    String restoreFile(
            Long fileId,
            String email);
    
    String permanentlyDeleteFile(
            Long fileId,
            String email);
    
    String moveFile(
            Long fileId,
            Long folderId,
            String email);
    String downloadFile(Long fileId, String email);
    
    String renameFile(Long fileId, String newFileName, String email);
    
}