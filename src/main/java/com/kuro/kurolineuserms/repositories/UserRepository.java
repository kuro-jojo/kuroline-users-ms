package com.kuro.kurolineuserms.repositories;

import com.kuro.kurolineuserms.data.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface UserRepository {

    void add(User user) throws ExecutionException, InterruptedException;

    User register(User user) throws Exception;

    User findById(String uid) throws ExecutionException, InterruptedException;

    void updateByName(User user);
    void updateByStatus(User user);
    void updateByProfilePicture(User user);

    List<User> findByName(String name) throws ExecutionException, InterruptedException;

    List<User> findByEmail(String email) throws ExecutionException, InterruptedException;

    User findByExactEmail(String email) throws ExecutionException, InterruptedException;

    void addContact(User user, String contactId);

    void removeContact(User user, String contactId);
}
