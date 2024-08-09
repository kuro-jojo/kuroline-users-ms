package com.kuro.kurolineuserms.data;

import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class User implements UserDetails {
    @Id
    private String id;
    private String name;
    private String email;
    private Status status;
    private String profilePhoto;
    private String phoneNumber;

    public User() {
    }

    public User(String name, String email, String profilePhoto) {
        this.name = name;
        this.email = email;
        this.status = Status.Offline;
        this.profilePhoto = profilePhoto;
    }

    public User(String name, String email, Status status, String profilePhoto) {
        this.name = name;
        this.email = email;
        this.status = status;
        this.profilePhoto = profilePhoto;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public boolean isNull() {
        return email == null || name == null || status == null;
    }

    public boolean isEmpty() {
        return email.isEmpty() || name.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("User info : \n id : %s, name : %s, email : %s, status : %s, profile : %s", id, name, email, status, profilePhoto);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
