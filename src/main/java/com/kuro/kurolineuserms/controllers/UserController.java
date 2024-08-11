package com.kuro.kurolineuserms.controllers;

import com.kuro.kurolineuserms.data.ResponseMessage;
import com.kuro.kurolineuserms.data.User;
import com.kuro.kurolineuserms.services.UserService;
import com.kuro.kurolineuserms.utils.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(path = "/api/v1")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/details")
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

    @PostMapping("/users/oauth")
    public ResponseEntity<ResponseMessage> signInWithOauth(@AuthenticationPrincipal User user) {
        if (user == null) {
            return new ResponseEntity<>(new ResponseMessage("No user provided"), HttpStatus.BAD_REQUEST);
        }

        try {
            User u = userService.get(user.getId());
            if (u != null) {
                return new ResponseEntity<>(new ResponseMessage("User found"), HttpStatus.OK);
            }

            userService.add(user);
            return new ResponseEntity<>(new ResponseMessage("User added successfully"), HttpStatus.CREATED);

        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<Object> signIn(@RequestBody User user, @AuthenticationPrincipal User authUser) {
        if (User.isNull(user)) {
            return new ResponseEntity<>("No user provided", HttpStatus.BAD_REQUEST);
        }
        if (User.isEmpty(user)) {
            return new ResponseEntity<>("Empty data", HttpStatus.BAD_REQUEST);
        }
        // check user email
        if (EmailValidator.isEmailInvalid(user.getEmail())) {
            return new ResponseEntity<>("Invalid email", HttpStatus.BAD_REQUEST);
        }

        try {
            if (userService.get(user.getId()) != null) {
                return new ResponseEntity<>("User already exists with this email address", HttpStatus.CONFLICT);
            }
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        user.setId(authUser.getId());
        try {
            userService.add(user);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>("User added successfully", HttpStatus.CREATED);
    }

//    @PatchMapping("/users/{id}")
//    public ResponseEntity<Object> edit(@RequestBody User user, @PathVariable String id){
//        if (id == null || id.isBlank()){
//            return new ResponseEntity<>("An id is required", HttpStatusCode.valueOf(400));
//        }
//        if(userService.find(id).equals(Optional.empty())){
//            return new ResponseEntity<>("User not found", HttpStatusCode.valueOf(404));
//        }
//        if (user.isEmpty()){
//            return new ResponseEntity<>("Empty data", HttpStatusCode.valueOf(400));
//        }
//        // check user email
//        if(EmailValidator.isEmailInvalid(user.getEmail()))
//        {
//            return new ResponseEntity<>("Invalid email", HttpStatusCode.valueOf(400));
//        }
//
//        user.setId(id);
//        userService.edit(user);
//        return new ResponseEntity<>("User edited successfully", HttpStatusCode.valueOf(200));
//    }
}
