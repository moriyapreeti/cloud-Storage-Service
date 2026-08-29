package com.cloudstorage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudstorage.entity.LinkShare;

public interface LinkShareRepository
        extends JpaRepository<LinkShare, Long> {

    Optional<LinkShare> findByToken(
            String token
    );

    List<LinkShare> findByFileId(
            Long fileId
    );

    List<LinkShare> findByOwnerId(
            Long ownerId
    );
}
