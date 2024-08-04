package com.kuro.kuroline.service.user;

import org.springframework.data.annotation.Id;

public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String status;
    private String profilePhoto;

    public User() {
    }

    public User(String name, String email, String status, String profilePhoto) {
        this.name = name;
        this.email = email;
        this.status = status;
        this.profilePhoto = profilePhoto;
    }

    public String getId(){
        return id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public boolean isNull(){
        return email == null || name == null || status == null;
    }

    public boolean isEmpty(){
        return email.isEmpty() || name.isEmpty() || status.isEmpty();
    }

    @Override
    public String toString(){
        return String.format("User info : \n id : %s, name : %s, email : %s, status : %s, profile : %s", id, name, email, status, profilePhoto);
    }
}
