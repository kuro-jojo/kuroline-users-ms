package com.kuro.kurolineuserms.repositories;

import com.google.firebase.auth.FirebaseAuthException;
import com.kuro.kurolineuserms.data.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface UserRepository {

    void add(User user) throws ExecutionException, InterruptedException;
    User register(User user) throws Exception;
    User get(String uid) throws ExecutionException, InterruptedException;

    void update(User user);

    User findByName(String name);

    User findByEmail(String email);

    List<User> findAllByName(String name);
}
