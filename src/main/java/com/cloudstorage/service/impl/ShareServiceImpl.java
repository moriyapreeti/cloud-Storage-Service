package com.cloudstorage.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudstorage.dto.request.CreateShareRequest;
import com.cloudstorage.dto.response.ShareResponse;
import com.cloudstorage.entity.CloudFile;
import com.cloudstorage.entity.Share;
import com.cloudstorage.entity.User;
import com.cloudstorage.repository.CloudFileRepository;
import com.cloudstorage.repository.ShareRepository;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.service.ShareService;

@Service
public class ShareServiceImpl
        implements ShareService {

    private final ShareRepository shareRepository;

    private final CloudFileRepository cloudFileRepository;

    private final UserRepository userRepository;

    public ShareServiceImpl(
            ShareRepository shareRepository,
            CloudFileRepository cloudFileRepository,
            UserRepository userRepository) {

        this.shareRepository = shareRepository;
        this.cloudFileRepository =
                cloudFileRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ShareResponse shareFile(
            Long fileId,
            CreateShareRequest request,
            String email) {

        User owner = getUser(email);

        CloudFile file =
                cloudFileRepository
                        .findByIdAndDeletedAtIsNull(fileId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "File not found"
                                ));

        checkOwner(file, owner);

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
                    "Owner cannot share file with himself"
            );
        }
        boolean alreadyShared =
                shareRepository
                        .existsByFileIdAndSharedWithUserId(
                                fileId,
                                targetUser.getId()
                        );

        if (alreadyShared) {

            throw new RuntimeException(
                    "File is already shared with this user"
            );
        }

        Share share = new Share();

        share.setFile(file);
        share.setOwner(owner);
        share.setSharedWithUser(targetUser);
        share.setPermission(
                request.getPermission()
        );

        Share savedShare =
                shareRepository.save(share);

        return convertToResponse(savedShare);
    }

    @Override
    public List<ShareResponse> getFileShares(
            Long fileId,
            String email) {

        User owner = getUser(email);

        CloudFile file =
                cloudFileRepository
                        .findByIdAndDeletedAtIsNull(fileId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "File not found"
                                ));

        checkOwner(file, owner);

        return shareRepository
                .findByFileId(fileId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String updatePermission(
            Long shareId,
            CreateShareRequest request,
            String email) {

        User owner = getUser(email);

        Share share =
                shareRepository
                        .findById(shareId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Share not found"
                                ));

        checkOwner(
                share.getFile(),
                owner
        );

        share.setPermission(
                request.getPermission()
        );

        shareRepository.save(share);

        return "Permission updated successfully";
    }

    @Override
    @Transactional
    public String removeShare(
            Long shareId,
            String email) {

        User owner = getUser(email);

        Share share =
                shareRepository
                        .findById(shareId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Share not found"
                                ));

        checkOwner(
                share.getFile(),
                owner
        );

        shareRepository.delete(share);

        return "File sharing removed";
    }

    @Override
    public List<ShareResponse> getSharedWithMe(
            String email) {

        User user = getUser(email);

        return shareRepository
                .findBySharedWithUserId(user.getId())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
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
            CloudFile file,
            User user) {

        if (!file.getOwner()
                .getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "Only file owner can manage sharing"
            );
        }
    }

    private ShareResponse convertToResponse(
            Share share) {

        User sharedUser =
                share.getSharedWithUser();

        return new ShareResponse(
                share.getId(),
                share.getFile().getId(),
                share.getFile().getFileName(),
                sharedUser.getEmail(),
                sharedUser.getName(),
                share.getPermission(),
                share.getCreatedAt()
        );
    }
}