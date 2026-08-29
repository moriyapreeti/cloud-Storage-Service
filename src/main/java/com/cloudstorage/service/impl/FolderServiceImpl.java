package com.cloudstorage.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudstorage.dto.request.CreateFolderRequest;
import com.cloudstorage.dto.response.FileResponse;
import com.cloudstorage.dto.response.FolderContentsResponse;
import com.cloudstorage.dto.response.FolderResponse;
import com.cloudstorage.entity.Folder;
import com.cloudstorage.entity.SharePermission;
import com.cloudstorage.entity.User;
import com.cloudstorage.repository.FolderRepository;
import com.cloudstorage.repository.FolderShareRepository;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.service.FolderService;


import com.cloudstorage.entity.CloudFile;
import com.cloudstorage.repository.CloudFileRepository;

@Service
public class FolderServiceImpl
        implements FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final CloudFileRepository cloudFileRepository;
    private final FolderShareRepository folderShareRepository;

    public FolderServiceImpl(
            FolderRepository folderRepository,
            UserRepository userRepository,
            CloudFileRepository cloudFileRepository,
            FolderShareRepository folderShareRepository) {

        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.cloudFileRepository = cloudFileRepository;
        this.folderShareRepository = folderShareRepository;
    }

    @Override
    @Transactional
    public FolderResponse createFolder(
            CreateFolderRequest request,
            String email) {

        User user = getUser(email);

        String folderName =
                request.getName().trim();

        if (request.getParentId() == null) {

            boolean exists =folderRepository.findByOwnerIdAndParentIsNullAndDeletedAtIsNull(user.getId()
                            ).stream().anyMatch(folder ->folder.getName().equalsIgnoreCase(folderName) );

            if (exists) {
                throw new RuntimeException("Folder already exists");
            }
       } else {
            Folder parent = getActiveFolder( request.getParentId());
            checkOwnership( parent,user);
            boolean exists =folderRepository.existsByOwnerIdAndParentIdAndNameIgnoreCaseAndDeletedAtIsNull(
                                    user.getId(),parent.getId(),folderName );

            if (exists) {
                throw new RuntimeException(
                        "Folder already exists"
                );
            }
        }
        Folder folder = new Folder();

        folder.setName(folderName);
        folder.setOwner(user);

        if (request.getParentId() != null) {

            Folder parent =
                    getActiveFolder(
                            request.getParentId()
                    );

            checkOwnership(
                    parent,
                    user
            );

            folder.setParent(parent);
        }

        Folder savedFolder =
                folderRepository.save(folder);

        return convertToResponse(savedFolder);
    }

    @Override
    public List<FolderResponse> getRootFolders(
            String email) {

        User user = getUser(email);

        return folderRepository
                .findByOwnerIdAndParentIsNullAndDeletedAtIsNull(
                        user.getId()
                )
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FolderResponse> getChildFolders(
            Long parentId,
            String email) {

        User user = getUser(email);

        Folder parent =
                getActiveFolder(parentId);

        checkOwnership(parent, user);

        return folderRepository
                .findByOwnerIdAndParentIdAndDeletedAtIsNull(
                        user.getId(),
                        parentId
                )
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FolderResponse getFolder(
            Long folderId,
            String email) {

        User user = getUser(email);

        Folder folder =
                getActiveFolder(folderId);

        checkOwnership(folder, user);

        return convertToResponse(folder);
    }

    @Override
    @Transactional
    public FolderResponse renameFolder(
            Long folderId,
            String newName,
            String email) {

        User user = getUser(email);

        Folder folder =
                getActiveFolder(folderId);

        checkOwnership(folder, user);

        String folderName =
                newName.trim();

        if (folderName.isBlank()) {
            throw new RuntimeException(
                    "Folder name is required"
            );
        }

        folder.setName(folderName);

        Folder savedFolder =
                folderRepository.save(folder);

        return convertToResponse(savedFolder);
    }

    @Override
    @Transactional
    public String deleteFolder(Long folderId, String email) {
        User user = getUser(email);

        Folder folder =getActiveFolder(folderId);

        checkOwnership(folder, user);
        softDeleteChildren(folder);
        folder.setDeletedAt(
                LocalDateTime.now()
        );

        folderRepository.save(folder);

        return "Folder moved to trash";
    }

    private User getUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));
    }

    private Folder getActiveFolder(
            Long folderId) {

        return folderRepository
                .findByIdAndDeletedAtIsNull(folderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Folder not found"
                        ));
    }

    private void checkOwnership(
            Folder folder,
            User user) {

        if (!folder.getOwner()
                .getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "You do not have permission to access this folder"
            );
        }
    }

    private FolderResponse convertToResponse(
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
    @Override
    public FolderContentsResponse getFolderContents(Long folderId,String email) {

        User user = getUser(email);

        Folder folder =getActiveFolder(folderId);

        checkOwnership(folder, user);

        List<FolderResponse> folders =
                folderRepository
                        .findByOwnerIdAndParentIdAndDeletedAtIsNull(
                                user.getId(),
                                folderId).stream()  .map(this::convertToResponse).collect(Collectors.toList());
    
        List<FileResponse> files =cloudFileRepository.findByOwnerIdAndFolderIdAndDeletedAtIsNull(user.getId(),folderId)                             
                        .stream().map(this::convertFileToResponse).collect(Collectors.toList());
                        
        return new FolderContentsResponse(
                folder.getId(),
                folder.getName(),
                folders,
                files
        );
    }
    private FileResponse convertFileToResponse(CloudFile file) {

        return new FileResponse(
                file.getId(),
                file.getFileName(),
                file.getContentType(),
                file.getFileSize(),
                null);
    }
    @Override
    @Transactional
    public String moveFolder(
            Long folderId,
            Long parentId,
            String email) {

        User user = getUser(email);

        Folder folder =getActiveFolder(folderId);

        checkOwnership(folder, user);

        if (parentId == null) {

            folder.setParent(null);

            folderRepository.save(folder);

            return "Folder moved successfully";
        }

        Folder newParent =getActiveFolder(parentId);

        checkOwnership(newParent, user);

        if (folder.getId().equals(newParent.getId())) {

            throw new RuntimeException(
                    "Folder cannot be moved inside itself"
            );
        }

        if (isChildFolder(newParent,folder)) {
            throw new RuntimeException( "Folder cannot be moved inside its own child"  );                            
        }

        folder.setParent(newParent);

        folderRepository.save(folder);

        return "Folder moved successfully";
    }
    private boolean isChildFolder(Folder possibleChild,Folder parentFolder) {
        Folder current = possibleChild;
        while (current != null) {

            if (current.getId().equals(parentFolder.getId())) {
                return true;
            }
           current = current.getParent();
        }
        return false;
    }
    private void softDeleteChildren(Folder parent) {

        List<Folder> children =folderRepository.findByParentIdAndDeletedAtIsNull(parent.getId());                                                                                           
        for (Folder child : children) {

            softDeleteChildren(child);

            child.setDeletedAt(LocalDateTime.now());
            folderRepository.save(child);
        }
    }
    private SharePermission getFolderPermission( Folder folder,User user) {

        if (folder.getOwner().getId().equals(user.getId())) {
            return null;
        }

        Folder current = folder;

        while (current != null) {

            var share =folderShareRepository.findByFolderIdAndSharedWithUserId( current.getId(),user.getId());

            if (share.isPresent()) {
                      return share.get()
                        .getPermission();
            }
            current = current.getParent();
        }
        throw new RuntimeException("You do not have access to this folder");
    }
    private boolean canReadFolder(Folder folder,User user) {

        if (folder.getOwner().getId().equals(user.getId())) {
            return true;
        }
        try {
            getFolderPermission(folder, user);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }private boolean canEditFolder( Folder folder,User user) {
        if (folder.getOwner().getId().equals(user.getId())) {
            return true;
        }
        try {
            SharePermission permission = getFolderPermission(folder, user );

            return permission== SharePermission.EDITOR;
        } catch (RuntimeException e) {
            return false;
        }
    }
}