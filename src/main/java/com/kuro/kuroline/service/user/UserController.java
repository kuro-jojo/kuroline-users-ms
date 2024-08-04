package com.kuro.kuroline.service.user;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAll(){
        return userService.findAll();
    }

    @PostMapping("/users")
    public String add(@RequestBody User user){
        if (user.isNull()){
            return "Must provide data" + user;
        }
        if (user.isEmpty()){
            return "Empty user must not be empty" + user;
        }
        userService.add(user);
        return "User added successfully";
    }
}
