package com.kuro.kurolineuserms.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.i18n.phonenumbers.NumberParseException;
import com.kuro.kurolineuserms.data.User;
import com.kuro.kurolineuserms.repositories.UserRepository;
import com.kuro.kurolineuserms.utils.EmailValidator;
import com.kuro.kurolineuserms.utils.PhoneValidator;
import com.kuro.kurolineuserms.utils.UserException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public User register(User user) throws FirebaseAuthException, NumberParseException, UserException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest();

        if (!user.getEmail().isBlank()) {
            if (EmailValidator.isEmailInvalid(user.getEmail())) {
                throw new UserException("Email is invalid");
            }
            if (user.getPassword().isBlank()) {
                throw new UserException("Password must not be empty");
            }
            request.setEmail(user.getEmail())
                    .setPassword(user.getPassword());
        } else if (!user.getPhoneNumber().isBlank()) {
            if (PhoneValidator.isValidPhoneNumber(user.getPhoneNumber())) {
                request.setPhoneNumber(user.getPhoneNumber());
            } else {
                throw new UserException("Invalid phone number");
            }
        } else {
            throw new UserException("Invalid user entry");
        }

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        user.setId(userRecord.getUid());
        return user;
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

    private void updateBy(String field, String val, String userID) {
        DocumentReference docRef = reference.document(userID);
        docRef.update(field, val);
    }

    @Override
    public void updateByName(User user) {
        updateBy("name", user.getName(), user.getId());
    }

    @Override
    public void updateByProfilePicture(User user) {
        updateBy("profilePicture", user.getProfilePicture(), user.getId());
    }

    @Override
    public List<User> findByName(String name) throws ExecutionException, InterruptedException {
        return findBy("name", name);
    }

    @Override
    public List<User> findByEmail(String email) throws ExecutionException, InterruptedException {

        return findBy("email", email);
    }

    @Override
    public User findByExactEmail(String email) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = reference.
                whereEqualTo("email", email).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        if(documents.isEmpty()){
            return null;
        }
        return documents.get(0).toObject(User.class);
    }

    private List<User> findBy(String field, String value) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = reference.
                whereGreaterThan(field, value.toUpperCase())
                .whereLessThan(field, value.toLowerCase() + '\uf8ff').get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<User> users = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            users.add(document.toObject(User.class));
        }
        return users;
    }
}
