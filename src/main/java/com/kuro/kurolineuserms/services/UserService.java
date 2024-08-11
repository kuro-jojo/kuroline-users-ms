package com.kuro.kurolineuserms.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.kuro.kurolineuserms.data.User;
import com.kuro.kurolineuserms.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService implements UserRepository {
    private final CollectionReference reference;

    public UserService(Firestore firestoreClient) {
        this.reference = firestoreClient.collection("users");
    }

    @Override
    public void add(User user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = reference.document(user.getId());
        docRef.set(user);
    }

    @Override
    public User get(String uid) throws ExecutionException, InterruptedException {
        DocumentReference docRef = reference.document(uid);

        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document;

        document = future.get();
        if (document.exists()) {
            return document.toObject(User.class);
        }
        return null;
    }

    @Override
    public void update(User user) {

    }

    @Override
    public User findByName(String name) {
        return null;
    }

    @Override
    public User findByEmail(String email) {
        return null;
    }

    @Override
    public List<User> findAllByName(String name) {
        return null;
    }
}
