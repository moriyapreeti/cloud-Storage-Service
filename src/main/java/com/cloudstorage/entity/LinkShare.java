package com.cloudstorage.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "link_shares")
public class LinkShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "file_id",
            nullable = false
    )
    private CloudFile file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "owner_id",
            nullable = false
    )
    private User owner;

    @Column(
            nullable = false,
            unique = true,
            length = 100
    )
    private String token;

    @Column(nullable = false)
    private boolean active;

    private LocalDateTime expiresAt;

    @Column(length = 255)
    private String passwordHash;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public LinkShare() {
    }

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CloudFile getFile() {
        return file;
    }

    public void setFile(CloudFile file) {
        this.file = file;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}