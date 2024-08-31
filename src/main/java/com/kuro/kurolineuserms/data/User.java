package com.kuro.kurolineuserms.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String name;
    private String email;
    private Status status;
    private String profilePicture;
    private String phoneNumber;
    private String password;
    private List<String> contacts;

    @Override
    public String toString() {
        return String.format("User info : \n id : %s, name : %s, email : %s, status : %s, profile : %s", id, name, email, status, profilePicture);
    }

    public static void getPublicInfo(User user){
        user.setPassword(null);
    }
}
