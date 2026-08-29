package com.cloudstorage.dto.response;

import java.util.List;

public class FolderContentsResponse {

    private Long folderId;
    private String folderName;

    private List<FolderResponse> folders;
    private List<FileResponse> files;

    public FolderContentsResponse() {
    }

    public FolderContentsResponse(
            Long folderId,
            String folderName,
            List<FolderResponse> folders,
            List<FileResponse> files) {

        this.folderId = folderId;
        this.folderName = folderName;
        this.folders = folders;
        this.files = files;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<FolderResponse> getFolders() {
        return folders;
    }

    public void setFolders(
            List<FolderResponse> folders) {
        this.folders = folders;
    }

    public List<FileResponse> getFiles() {
        return files;
    }

    public void setFiles(
            List<FileResponse> files) {
        this.files = files;
    }
}