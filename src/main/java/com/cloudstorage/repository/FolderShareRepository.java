package com.cloudstorage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudstorage.entity.FolderShare;

public interface FolderShareRepository
        extends JpaRepository<FolderShare, Long> {

    Optional<FolderShare>
    findByFolderIdAndSharedWithUserId(
            Long folderId,
            Long userId
    );

    List<FolderShare>
    findByFolderId(
            Long folderId
    );

    List<FolderShare>
    findBySharedWithUserId(
            Long userId
    );

    boolean existsByFolderIdAndSharedWithUserId(
            Long folderId,
            Long userId
    );
}