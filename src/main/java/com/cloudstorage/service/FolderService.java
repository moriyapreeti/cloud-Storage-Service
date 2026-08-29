package com.cloudstorage.service;

import java.util.List;

import com.cloudstorage.dto.request.CreateFolderRequest;
import com.cloudstorage.dto.response.FolderContentsResponse;
import com.cloudstorage.dto.response.FolderResponse;

public interface FolderService {

    FolderResponse createFolder(
            CreateFolderRequest request,
            String email);

    List<FolderResponse> getRootFolders(
            String email);

    List<FolderResponse> getChildFolders(
            Long parentId,
            String email);

    FolderResponse getFolder(
            Long folderId,
            String email);

    FolderResponse renameFolder(
            Long folderId,
            String newName,
            String email);

    String deleteFolder(
            Long folderId,
            String email);
    
    FolderContentsResponse getFolderContents(
            Long folderId,
            String email);
    String moveFolder(
            Long folderId,
            Long parentId,
            String email);

}