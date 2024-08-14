package com.kuro.kurolineuserms.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
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
