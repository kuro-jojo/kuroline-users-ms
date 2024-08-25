package com.kuro.kurolineuserms.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public static boolean isNull(User user) {
        return user.getEmail() == null || user.getName() == null || user.getStatus() == null;
    }

    public static boolean isEmpty(User user) {
        return user.getEmail().isEmpty() || user.getName().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("User info : \n id : %s, name : %s, email : %s, status : %s, profile : %s", id, name, email, status, profilePicture);
    }
}
