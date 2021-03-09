package com.alpha.messenger.persistance.model;

import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

public class User {

    private String fullName;
    private String username;
    private String password;
    private String mail;
    private String image;
    private Boolean statusAccount;

    public Boolean getStatusAccount() {
        return statusAccount;
    }

    public void setStatusAccount(Boolean statusAccount) {
        this.statusAccount = statusAccount;
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

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("User [username=").append(username)
                .append(", mail=").append(mail)
                .append(", fullName=").append(fullName)
                .append("]");
        return builder.toString();
    }
}