package com.kuro.kurolineuserms.controllers;

import com.kuro.kurolineuserms.data.ResponseMessage;
import com.kuro.kurolineuserms.data.Status;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * UserController handles all requests for managing a user.
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {
    private final UserService userService;
    private final FileService fileService;

    public UserController(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    /**
     * Gets the current user details.
     *
     * @param user the authenticated user
     * @return the user details
     */
    @GetMapping("/details")
    public ResponseEntity<Object> getCurrentUserDetails(@AuthenticationPrincipal User user) {
        try {
            User foundUser = userService.findById(user.getId());
            User.getPublicInfo(foundUser);
            return new ResponseEntity<>(foundUser, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching user details", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets user details by ID.
     *
     * @param userId the user ID
     * @return the user details
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<Object> getUserDetails(@PathVariable("id") String userId) {
        try {
            User user = userService.findById(userId);
            User.getPublicInfo(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error fetching user details", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the user's name.
     *
     * @param name the new name
     * @param user the authenticated user
     * @return the response entity
     */
    @PutMapping("/name")
    public ResponseEntity<Object> updateName(@RequestBody Map<String, String> name,
            @AuthenticationPrincipal User user) {
        String newName = name.get("name");
        if (newName == null || newName.isBlank()) {
            return new ResponseEntity<>(new ResponseMessage("Name not provided"), HttpStatus.BAD_REQUEST);
        }
        user.setName(newName);
        userService.updateByName(user);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * Updates the user's profile picture.
     *
     * @param file the new profile picture file
     * @param user the authenticated user
     * @return the response entity
     */
    @PutMapping("/picture")
    public ResponseEntity<Object> updateProfilePicture(@RequestPart(value = "picture") MultipartFile file,
            @AuthenticationPrincipal User user) {
        try {
            String pictureUrl = fileService.upload(file, user.getId());
            user.setProfilePicture(pictureUrl);
            userService.updateByProfilePicture(user);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error uploading profile picture", e);
            return new ResponseEntity<>(new ResponseMessage("Cannot upload the profile picture"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Searches for users by type and query.
     *
     * @param type     the search type (email or name)
     * @param query    the search query
     * @param withUser whether to include the authenticated user in the results
     * @param user     the authenticated user
     * @return the list of users
     */
    @GetMapping("")
    public ResponseEntity<Object> searchBy(@RequestParam(name = "t") String type,
            @RequestParam(name = "q") String query, @RequestParam(name = "u", defaultValue = "false") String withUser,
            @AuthenticationPrincipal User user) {
        try {
            List<User> users;
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
            if (!Boolean.parseBoolean(withUser)) {
                users.removeIf(u -> u.getId().equals(user.getId()));
            }
            users.forEach(User::getPublicInfo);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error searching users", e);
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Adds a contact for the authenticated user.
     *
     * @param contactId the contact ID
     * @param user      the authenticated user
     * @return the response entity
     */
    @PatchMapping("/contacts/{id}")
    public ResponseEntity<Object> addContact(@PathVariable(value = "id") String contactId,
            @AuthenticationPrincipal User user) {
        try {
            Optional<User> contactOpt = Optional.ofNullable(userService.findById(contactId));
            if (contactOpt.isEmpty()) {
                return new ResponseEntity<>(new ResponseMessage("User not found for the id provided"),
                        HttpStatus.BAD_REQUEST);
            }
            User contact = contactOpt.get();
            userService.addContact(user, contact.getId());
            userService.addContact(contact, user.getId());
            return new ResponseEntity<>(new ResponseMessage("Contact added successfully"), HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error adding contact", e);
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the user's status.
     *
     * @param status the new status
     * @param user   the authenticated user
     * @return the response entity
     */
    @PatchMapping("/status")
    public ResponseEntity<Object> updateStatus(@RequestBody Map<String, String> status,
            @AuthenticationPrincipal User user) {
        try {
            user.setStatus(Status.valueOf(status.get("status")));
            userService.updateByStatus(user);
            return new ResponseEntity<>(new ResponseMessage("Status updated successfully"), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.warn("Incorrect status provided", e);
            return new ResponseEntity<>(new ResponseMessage("Incorrect status"), HttpStatus.BAD_REQUEST);
        }
    }
}