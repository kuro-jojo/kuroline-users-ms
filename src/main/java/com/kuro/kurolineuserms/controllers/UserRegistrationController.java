package com.kuro.kurolineuserms.controllers;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.kuro.kurolineuserms.data.ResponseMessage;
import com.kuro.kurolineuserms.data.User;
import com.kuro.kurolineuserms.services.FileService;
import com.kuro.kurolineuserms.services.UserService;
import com.kuro.kurolineuserms.utils.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/users/register")
public class UserRegistrationController {
    private final UserService userService;
    private final FileService fileService;

    public UserRegistrationController(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    /**
     * Registers a new user with email and optional profile picture.
     *
     * @param file the profile picture file
     * @param user the user details
     * @return the response entity with a message and HTTP status
     */
    @PostMapping("/basic")
    public ResponseEntity<ResponseMessage> signUpWithEmail(
            @RequestPart(value = "picture", required = false) MultipartFile file,
            @RequestPart("user") User user) {
        if (user == null) {
            log.warn("No user provided");
            return new ResponseEntity<>(new ResponseMessage("No user provided"), HttpStatus.BAD_REQUEST);
        }

        try {
            if (isUserExists(user.getEmail())) {
                log.warn("User already exists");
                return new ResponseEntity<>(new ResponseMessage("User already exists"), HttpStatus.CONFLICT);
            }

            user = userService.register(user);
            if (file != null && !file.isEmpty()) {
                user.setProfilePicture(fileService.upload(file, user.getId()));
            }
            userService.add(user);
            log.info("New user registered successfully");
            return new ResponseEntity<>(new ResponseMessage("User registered successfully"), HttpStatus.CREATED);

        } catch (FirebaseAuthException e) {
            return handleFirebaseAuthException(e);
        } catch (IOException e) {
            log.error("Cannot upload the profile picture: {}", e.getMessage());
            return new ResponseEntity<>(new ResponseMessage("Cannot upload the profile picture"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NumberParseException | UserException e) {
            log.error("Error: {}", e.getMessage());
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Signs in a user with OAuth.
     *
     * @param user the authenticated user
     * @return the response entity with a message and HTTP status
     */
    @PostMapping("/oauth")
    public ResponseEntity<ResponseMessage> signInWithOauth(@AuthenticationPrincipal User user) {
        if (user == null) {
            log.warn("No user provided");
            return new ResponseEntity<>(new ResponseMessage("No user provided"), HttpStatus.BAD_REQUEST);
        }

        try {
            if (isUserExistsById(user.getId())) {
                return new ResponseEntity<>(new ResponseMessage("User found"), HttpStatus.OK);
            }
            userService.add(user);
            log.info("User added successfully");
            return new ResponseEntity<>(new ResponseMessage("User added successfully"), HttpStatus.CREATED);

        } catch (ExecutionException | InterruptedException e) {
            log.error("Error: {}", e.getMessage());
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Checks if a user exists by email.
     *
     * @param email the email to check
     * @return true if the user exists, false otherwise
     * @throws ExecutionException   if an error occurs during execution
     * @throws InterruptedException if the operation is interrupted
     */
    private boolean isUserExists(String email) throws ExecutionException, InterruptedException {
        return userService.findByExactEmail(email) != null;
    }

    /**
     * Checks if a user exists by ID.
     *
     * @param userId the user ID to check
     * @return true if the user exists, false otherwise
     * @throws ExecutionException   if an error occurs during execution
     * @throws InterruptedException if the operation is interrupted
     */
    private boolean isUserExistsById(String userId) throws ExecutionException, InterruptedException {
        return userService.findById(userId) != null;
    }

    /**
     * Handles Firebase authentication exceptions.
     *
     * @param e the FirebaseAuthException
     * @return the response entity with a message and HTTP status
     */
    private ResponseEntity<ResponseMessage> handleFirebaseAuthException(FirebaseAuthException e) {
        String msg = e.getMessage();
        if (e.getAuthErrorCode() != null) {
            if (e.getAuthErrorCode().equals(AuthErrorCode.EMAIL_ALREADY_EXISTS)) {
                msg = "Email already exists";
            } else if (e.getAuthErrorCode().equals(AuthErrorCode.PHONE_NUMBER_ALREADY_EXISTS)) {
                msg = "Phone number already exists";
            }
        }
        log.error("FirebaseAuthException: {}", msg);
        return new ResponseEntity<>(new ResponseMessage(msg), HttpStatus.BAD_REQUEST);
    }
}