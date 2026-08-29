package com.cloudstorage.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cloudstorage.dto.response.DriveResponse;
import com.cloudstorage.dto.response.FileResponse;
import com.cloudstorage.dto.response.FolderResponse;
import com.cloudstorage.entity.CloudFile;
import com.cloudstorage.entity.Folder;
import com.cloudstorage.entity.User;
import com.cloudstorage.repository.CloudFileRepository;
import com.cloudstorage.repository.FolderRepository;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.service.DriveService;

@Service
public class DriveServiceImpl
        implements DriveService {

    private final UserRepository userRepository;

    private final FolderRepository folderRepository;

    private final CloudFileRepository cloudFileRepository;

    public DriveServiceImpl(
            UserRepository userRepository,
            FolderRepository folderRepository,
            CloudFileRepository cloudFileRepository) {

        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.cloudFileRepository = cloudFileRepository;
    }

    @Override
    public DriveResponse getMyDrive(
            String email) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        List<FolderResponse> folders =
                folderRepository
                        .findByOwnerIdAndParentIsNullAndDeletedAtIsNull(
                                user.getId()
                        )
                        .stream()
                        .map(this::convertFolder)
                        .collect(Collectors.toList());

        List<FileResponse> files =
                cloudFileRepository
                        .findByOwnerIdAndFolderIsNullAndDeletedAtIsNull(
                                user.getId()
                        )
                        .stream()
                        .map(this::convertFile)
                        .collect(Collectors.toList());

        return new DriveResponse(
                folders,
                files
        );
    }

    private FolderResponse convertFolder(
            Folder folder) {

        Long parentId = null;

        if (folder.getParent() != null) {
            parentId =
                    folder.getParent().getId();
        }

        return new FolderResponse(
                folder.getId(),
                folder.getName(),
                parentId,
                folder.getCreatedAt(),
                folder.getUpdatedAt()
        );
    }

    private FileResponse convertFile(
            CloudFile file) {

        return new FileResponse(
                file.getId(),
                file.getFileName(),
                file.getContentType(),
                file.getFileSize(),
                null
        );
    }
}