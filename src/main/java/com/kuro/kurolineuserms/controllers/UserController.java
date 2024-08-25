package com.kuro.kurolineuserms.controllers;

import com.kuro.kurolineuserms.data.ResponseMessage;
import com.kuro.kurolineuserms.data.User;
import com.kuro.kurolineuserms.services.FileService;
import com.kuro.kurolineuserms.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * User controller : Handles all requests for managing a user
 */
@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {
    private final UserService userService;
    private final FileService fileService;

    public UserController(
            UserService userService,
            FileService fileService
    ) {
        this.userService = userService;
        this.fileService = fileService;
    }

    /**
     * Gets user details.
     *
     * @param user the user
     * @return the user details
     */
    @GetMapping("/details")
    public ResponseEntity<Object> getUserDetails(@AuthenticationPrincipal User user) {
        try {
            user = userService.get(user.getId());
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ResponseMessage("User not found"), HttpStatus.NOT_FOUND);
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/name")
    public ResponseEntity<Object> updateName(
            @RequestBody Map<String, String> name,
            @AuthenticationPrincipal User user) {
        if (name.get("name").isBlank()) {
            return new ResponseEntity<>(new ResponseMessage("Name not provided"), HttpStatus.BAD_REQUEST);
        }
        user.setName(name.get("name"));
        userService.updateByName(user);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PutMapping("/picture")
    public ResponseEntity<Object> updateProfilePicture(
            @RequestPart(value = "picture") MultipartFile file,
            @AuthenticationPrincipal User user
    ) {
        try {
            String  picture = fileService.upload(file, user.getId());
            System.out.println("Updating profile : " + picture);
            user.setProfilePicture(picture);
            userService.updateByProfilePicture(user);
        } catch (IOException e) {
            return new ResponseEntity<>(new ResponseMessage("Cannot upload the profile picture"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
