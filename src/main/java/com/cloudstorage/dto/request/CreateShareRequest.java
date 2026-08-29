package com.cloudstorage.dto.request;

import com.cloudstorage.entity.SharePermission;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateShareRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotNull(message = "Permission is required")
    private SharePermission permission;

    public CreateShareRequest() {
    }

    public CreateShareRequest(
            String email,
            SharePermission permission) {

        this.email = email;
        this.permission = permission;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SharePermission getPermission() {
        return permission;
    }

    public void setPermission(
            SharePermission permission) {

        this.permission = permission;
    }
}