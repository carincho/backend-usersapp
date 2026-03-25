package com.carincho.backend.usersapp.backendusersapp.models.request;

import com.carincho.backend.usersapp.backendusersapp.models.IUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserRequest implements IUser { 

    @NotBlank
    @Size(min = 4, max = 8)
    private String username;
    private boolean admin;

    @NotEmpty
    @Email
    private String email;

    

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    

}
