package com.alpha.messenger.web.dto;

import com.alpha.messenger.validation.PasswordMatches;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserDto {
    @NotNull
    @Size(min = 1, message = "{Size.userDto.fullName}")
    private String fullName;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.firstName}")
    private String username;

    @NotNull
    @Size(min = 1)
    private String password;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.mail}")
    private String mail;

    @NotNull
    private String image;

    private String status;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFullName() {
        return fullName;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return mail;
    }

    public void setEmail(final String mail) {
        this.mail = mail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("UserDto [username=")
                .append(username)
                .append(", mail=")
                .append(mail)
                .append("]");
        return builder.toString();
    }
}