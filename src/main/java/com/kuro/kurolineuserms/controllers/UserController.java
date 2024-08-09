package com.kuro.kurolineuserms.controllers;

import com.kuro.kurolineuserms.data.User;
import com.kuro.kurolineuserms.services.UserService;
import com.kuro.kurolineuserms.utils.EmailValidator;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getAll(){
        return new ResponseEntity<>(userService.findAll(), HttpStatusCode.valueOf(200));
    }

    @PostMapping("/users")
    public ResponseEntity<Object> add(@RequestBody User user){
        if (user.isNull()){
            return new ResponseEntity<>("No user provided", HttpStatusCode.valueOf(400));
        }
        if (user.isEmpty()){
            return new ResponseEntity<>("Empty data", HttpStatusCode.valueOf(400));
        }
        // check user email
        if(EmailValidator.isEmailInvalid(user.getEmail()))
        {
            return new ResponseEntity<>("Invalid email", HttpStatusCode.valueOf(400));
        }

        if (userService.findByEmail(user.getEmail()) != null) {
            return new ResponseEntity<>("User already exists with this email address", HttpStatusCode.valueOf(409));
        }

        userService.add(user);
        return  new ResponseEntity<>("User added successfully", HttpStatusCode.valueOf(201));
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<Object> edit(@RequestBody User user, @PathVariable String id){
        if (id == null || id.isBlank()){
            return new ResponseEntity<>("An id is required", HttpStatusCode.valueOf(400));
        }
        if(userService.find(id).equals(Optional.empty())){
            return new ResponseEntity<>("User not found", HttpStatusCode.valueOf(404));
        }
        if (user.isEmpty()){
            return new ResponseEntity<>("Empty data", HttpStatusCode.valueOf(400));
        }
        // check user email
        if(EmailValidator.isEmailInvalid(user.getEmail()))
        {
            return new ResponseEntity<>("Invalid email", HttpStatusCode.valueOf(400));
        }

        user.setId(id);
        userService.edit(user);
        return new ResponseEntity<>("User edited successfully", HttpStatusCode.valueOf(200));
    }
}
