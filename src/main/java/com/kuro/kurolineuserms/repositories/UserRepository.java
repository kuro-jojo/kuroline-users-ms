package com.kuro.kurolineuserms.repositories;

import com.kuro.kurolineuserms.data.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

    public User findByName(String name);

    public User findByEmail(String email);

    public List<User> findAllByName(String name);
}
