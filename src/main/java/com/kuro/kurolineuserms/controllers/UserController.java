package com.kuro.kurolineuserms.controllers;

import com.kuro.kurolineuserms.data.ResponseMessage;
import com.kuro.kurolineuserms.data.User;
import com.kuro.kurolineuserms.services.FileService;
import com.kuro.kurolineuserms.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * User controller : Handles all requests for managing a user
 */
@Slf4j
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
    public ResponseEntity<Object> getCurrentUserDetails(@AuthenticationPrincipal User user) {
        try {
            user = userService.get(user.getId());
            User.getPublicInfo(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<Object> getUserDetails(@PathVariable("id") String userId) {
        User user;
        try {
            user = userService.get(userId);
            User.getPublicInfo(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            String picture = fileService.upload(file, user.getId());
            System.out.println("Updating profile : " + picture);
            user.setProfilePicture(picture);
            userService.updateByProfilePicture(user);
        } catch (IOException e) {
            return new ResponseEntity<>(new ResponseMessage("Cannot upload the profile picture"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<Object> searchBy(
            @RequestParam(name = "t") String type,
            @RequestParam(name = "q") String query,
            @RequestParam(name = "u", defaultValue = "false") String withUser,
            @AuthenticationPrincipal User user
    ) {
        List<User> users;
        try {
            switch (type) {
                case "email":
                    users = userService.findByEmail(query);
                    break;
                case "name":
                    users = userService.findByName(query);
                    break;
                default:
                    return new ResponseEntity<>(new ResponseMessage("Wrong query type"), HttpStatus.BAD_REQUEST);
            }
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Remove private info of a user
        users.removeIf(u -> u.getId().equals(user.getId()));
        for (User u : users) {
            User.getPublicInfo(u);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
