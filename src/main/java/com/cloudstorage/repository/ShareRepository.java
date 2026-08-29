package com.cloudstorage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudstorage.entity.Share;

public interface ShareRepository
        extends JpaRepository<Share, Long> {

    Optional<Share> findByFileIdAndSharedWithUserId(
            Long fileId,
            Long userId
    );

    List<Share> findByFileId(
            Long fileId
    );

    List<Share> findBySharedWithUserId(
            Long userId
    );

    boolean existsByFileIdAndSharedWithUserId(
            Long fileId,
            Long userId
    );
}