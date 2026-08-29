package com.cloudstorage.service;

import java.util.List;

import com.cloudstorage.dto.request.CreateFolderShareRequest;
import com.cloudstorage.dto.response.FolderShareResponse;

public interface FolderShareService {

    FolderShareResponse shareFolder(
            Long folderId,
            CreateFolderShareRequest request,
            String email);

    List<FolderShareResponse> getFolderShares(
            Long folderId,
            String email);

    String updatePermission(
            Long shareId,
            CreateFolderShareRequest request,
            String email);

    String removeShare(
            Long shareId,
            String email);
}