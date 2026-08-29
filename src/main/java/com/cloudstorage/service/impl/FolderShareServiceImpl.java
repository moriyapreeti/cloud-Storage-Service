package com.cloudstorage.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudstorage.dto.request.CreateFolderShareRequest;
import com.cloudstorage.dto.response.FolderShareResponse;
import com.cloudstorage.entity.Folder;
import com.cloudstorage.entity.FolderShare;
import com.cloudstorage.entity.User;
import com.cloudstorage.repository.FolderRepository;
import com.cloudstorage.repository.FolderShareRepository;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.service.FolderShareService;

@Service
public class FolderShareServiceImpl
        implements FolderShareService {

    private final FolderShareRepository
            folderShareRepository;

    private final FolderRepository
            folderRepository;

    private final UserRepository
            userRepository;

    public FolderShareServiceImpl(
            FolderShareRepository folderShareRepository,
            FolderRepository folderRepository,
            UserRepository userRepository) {

        this.folderShareRepository =
                folderShareRepository;

        this.folderRepository =
                folderRepository;

        this.userRepository =
                userRepository;
    }

    @Override
    @Transactional
    public FolderShareResponse shareFolder(
            Long folderId,
            CreateFolderShareRequest request,
            String email) {

        User owner = getUser(email);

        Folder folder =
                folderRepository
                        .findByIdAndDeletedAtIsNull(
                                folderId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Folder not found"
                                ));

        checkOwner(folder, owner);

        User targetUser =
                userRepository
                        .findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User with this email not found"
                                ));

        if (targetUser.getId()
                .equals(owner.getId())) {

            throw new RuntimeException(
                    "Owner cannot share folder with himself"
            );
        }

        boolean alreadyShared =
                folderShareRepository
                        .existsByFolderIdAndSharedWithUserId(
                                folderId,
                                targetUser.getId()
                        );

        if (alreadyShared) {

            throw new RuntimeException(
                    "Folder is already shared with this user"
            );
        }

        FolderShare folderShare =
                new FolderShare();

        folderShare.setFolder(folder);
        folderShare.setOwner(owner);
        folderShare.setSharedWithUser(targetUser);
        folderShare.setPermission(
                request.getPermission()
        );

        FolderShare saved =
                folderShareRepository
                        .save(folderShare);

        return convertToResponse(saved);
    }

    @Override
    public List<FolderShareResponse>
    getFolderShares(
            Long folderId,
            String email) {

        User owner = getUser(email);

        Folder folder =
                folderRepository
                        .findByIdAndDeletedAtIsNull(
                                folderId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Folder not found"
                                ));

        checkOwner(folder, owner);

        return folderShareRepository
                .findByFolderId(folderId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String updatePermission(
            Long shareId,
            CreateFolderShareRequest request,
            String email) {

        User owner = getUser(email);

        FolderShare share =
                folderShareRepository
                        .findById(shareId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Folder share not found"
                                ));

        checkOwner(
                share.getFolder(),
                owner
        );

        share.setPermission(
                request.getPermission()
        );

        folderShareRepository.save(share);

        return "Folder permission updated successfully";
    }

    @Override
    @Transactional
    public String removeShare(
            Long shareId,
            String email) {

        User owner = getUser(email);

        FolderShare share =
                folderShareRepository
                        .findById(shareId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Folder share not found"
                                ));

        checkOwner(
                share.getFolder(),
                owner
        );

        folderShareRepository.delete(share);

        return "Folder sharing removed";
    }

    private User getUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));
    }

    private void checkOwner(
            Folder folder,
            User user) {

        if (!folder.getOwner()
                .getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "Only folder owner can manage sharing"
            );
        }
    }

    private FolderShareResponse
    convertToResponse(
            FolderShare share) {

        User sharedUser =
                share.getSharedWithUser();

        return new FolderShareResponse(
                share.getId(),
                share.getFolder().getId(),
                share.getFolder().getName(),
                sharedUser.getEmail(),
                sharedUser.getName(),
                share.getPermission(),
                share.getCreatedAt()
        );
    }
}