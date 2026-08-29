package com.cloudstorage.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(
    name = "folder_shares",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "folder_id",
                "shared_with_user_id"
            })
    })
public class FolderShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "folder_id",
        nullable = false
    )
    private Folder folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "owner_id",
        nullable = false
    )
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "shared_with_user_id",
        nullable = false
    )
    private User sharedWithUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SharePermission permission;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public FolderShare() {
    }

    @PrePersist
    public void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {

        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getSharedWithUser() {
        return sharedWithUser;
    }

    public void setSharedWithUser(
            User sharedWithUser) {

        this.sharedWithUser = sharedWithUser;
    }

    public SharePermission getPermission() {
        return permission;
    }

    public void setPermission(
            SharePermission permission) {

        this.permission = permission;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt = updatedAt;
    }
}