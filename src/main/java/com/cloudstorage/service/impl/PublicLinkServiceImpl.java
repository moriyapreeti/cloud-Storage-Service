package com.cloudstorage.service.impl;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudstorage.dto.request.CreatePublicLinkRequest;
import com.cloudstorage.dto.request.PublicLinkAccessRequest;
import com.cloudstorage.dto.response.PublicLinkAccessResponse;
import com.cloudstorage.dto.response.PublicLinkResponse;
import com.cloudstorage.entity.CloudFile;
import com.cloudstorage.entity.LinkShare;
import com.cloudstorage.entity.User;
import com.cloudstorage.repository.CloudFileRepository;
import com.cloudstorage.repository.LinkShareRepository;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.service.PublicLinkService;
import com.cloudstorage.service.SupabaseStorageService;

@Service
public class PublicLinkServiceImpl implements PublicLinkService {

    private final LinkShareRepository linkShareRepository;
    private final CloudFileRepository cloudFileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SupabaseStorageService supabaseStorageService;

    public PublicLinkServiceImpl(
            LinkShareRepository linkShareRepository,
            CloudFileRepository cloudFileRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            SupabaseStorageService supabaseStorageService) {

        this.linkShareRepository =linkShareRepository;
        this.cloudFileRepository =cloudFileRepository;
        this.userRepository =userRepository;
        this.passwordEncoder =passwordEncoder;
       this.supabaseStorageService = supabaseStorageService;
    }

    @Override
    @Transactional
    public PublicLinkResponse createLink(
            Long fileId,
            CreatePublicLinkRequest request,
            String email) {

        User owner = getUser(email);

        CloudFile file =
                cloudFileRepository
                        .findByIdAndDeletedAtIsNull(
                                fileId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "File not found"
                                ));

        if (!file.getOwner()
                .getId()
                .equals(owner.getId())) {

            throw new RuntimeException(
                    "Only file owner can create public link"
            );
        }

        LinkShare link =
                new LinkShare();

        link.setFile(file);
        link.setOwner(owner);
        link.setToken(generateToken());
        link.setActive(true);
        link.setExpiresAt(
                request.getExpiresAt()
        );

        if (request.getPassword() != null
                && !request.getPassword()
                        .isBlank()) {

            link.setPasswordHash(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );
        }

        LinkShare saved =linkShareRepository.save(link);

        return convertToResponse(saved);
    }

    @Override
    public List<PublicLinkResponse>getFileLinks(
            Long fileId,
            String email) {

        User owner = getUser(email);

        CloudFile file =
                cloudFileRepository
                        .findByIdAndDeletedAtIsNull(
                                fileId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "File not found"
                                ));

        if (!file.getOwner()
                .getId()
                .equals(owner.getId())) {

            throw new RuntimeException(
                    "Only owner can view public links"
            );
        }

        return linkShareRepository
                .findByFileId(fileId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String disableLink(
            Long linkId,
            String email) {

        User owner = getUser(email);

        LinkShare link =
                linkShareRepository
                        .findById(linkId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Public link not found"
                                ));

        if (!link.getOwner()
                .getId()
                .equals(owner.getId())) {

            throw new RuntimeException(
                    "Only owner can disable link"
            );
        }

        link.setActive(false);

        linkShareRepository.save(link);

        return "Public link disabled";
    }

   /* @Override
    public PublicLinkAccessResponse accessLink(
            String token,
            PublicLinkAccessRequest request) {

        LinkShare link = linkShareRepository.findByToken(token).orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid public link"
                                ));

        if (!link.isActive()) {

            throw new RuntimeException(
                    "Public link is disabled"
            );
        }

        if (link.getFile()
                .getDeletedAt() != null) {

            throw new RuntimeException(
                    "File is in trash"
            );
        }

        if (link.getExpiresAt() != null
                && link.getExpiresAt()
                        .isBefore(
                                java.time.LocalDateTime.now()
                        )) {

            throw new RuntimeException(
                    "Public link has expired"
            );
        }

        if (link.getPasswordHash() != null) {

            if (request == null
                    || request.getPassword() == null) {

                throw new RuntimeException(
                        "Password is required"
                );
            }

            boolean valid =
                    passwordEncoder.matches(
                            request.getPassword(),
                            link.getPasswordHash()
                    );

            if (!valid) {

                throw new RuntimeException(
                        "Invalid password"
                );
            }
        }

        String downloadUrl =supabaseStorageService.generateSignedUrl(file.getStoragePath());

        return new PublicLinkAccessResponse(
                file.getFileName(),
                downloadUrl,
                3600
        );
    }*/@Override
    public PublicLinkAccessResponse accessLink(
            String token,PublicLinkAccessRequest request) {

        LinkShare link =linkShareRepository.findByToken(token).orElseThrow(() ->new RuntimeException("Invalid public link"));

        if (!link.isActive()) {

            throw new RuntimeException("Public link is disabled");
        }
        CloudFile file = link.getFile();

        if (file.getDeletedAt() != null) {

            throw new RuntimeException("File is in trash");
        }

        if (link.getExpiresAt() != null&& link.getExpiresAt().isBefore(java.time.LocalDateTime.now()
                        )) {

            throw new RuntimeException("Public link has expired");
        }

        if (link.getPasswordHash() != null) {

            if (request == null|| request.getPassword() == null || request.getPassword().isBlank()) {

                throw new RuntimeException("Password is required");
            }

            boolean valid =passwordEncoder.matches(request.getPassword(),link.getPasswordHash()
                    );

            if (!valid) {

                throw new RuntimeException( "Invalid password");
            }
        }

        String downloadUrl =supabaseStorageService.createSignedUrl(file.getStoragePath(),3600);

        return new PublicLinkAccessResponse(
                file.getFileName(),
                downloadUrl,
                3600
        );
    }
   private User getUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));
    }

    private String generateToken() {

        SecureRandom random =
                new SecureRandom();

        byte[] bytes = new byte[32];

        random.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private PublicLinkResponse
    convertToResponse(
            LinkShare link) {

        return new PublicLinkResponse(
                link.getId(),
                link.getFile().getId(),
                link.getFile().getFileName(),
                link.getToken(),
                "/api/public-links/"
                        + link.getToken(),
                link.getExpiresAt(),
                link.getPasswordHash() != null,
                link.isActive(),
                link.getCreatedAt()
        );
    }
}