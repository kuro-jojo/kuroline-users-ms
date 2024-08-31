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

/**
 * Service class for managing users.
 */
@Service
public class UserService implements UserRepository {
    private final CollectionReference reference;

    /**
     * Constructor for UserService.
     *
     * @param firestoreClient Firestore client instance.
     */
    public UserService(Firestore firestoreClient) {
        this.reference = firestoreClient.collection("users");
    }

    /**
     * Adds a new user to the Firestore database.
     *
     * @param user The user to add.
     * @throws ExecutionException   If the computation threw an exception.
     * @throws InterruptedException If the current thread was interrupted while
     *                              waiting.
     */
    @Override
    public void add(User user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = reference.document(user.getId());
        docRef.set(user);
    }

    /**
     * Registers a new user with Firebase Authentication and sets the user ID.
     *
     * @param user The user to register.
     * @return The registered user with the user ID set.
     * @throws FirebaseAuthException If there is an error with Firebase
     *                               Authentication.
     * @throws NumberParseException  If there is an error parsing the phone number.
     * @throws UserException         If the user data is invalid.
     */
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

    /**
     * Finds a user by their ID.
     *
     * @param uid The user ID.
     * @return The user, or null if not found.
     * @throws ExecutionException   If the computation threw an exception.
     * @throws InterruptedException If the current thread was interrupted while
     *                              waiting.
     */
    @Override
    public User findById(String uid) throws ExecutionException, InterruptedException {
        DocumentReference docRef = reference.document(uid);

        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document;

        document = future.get();
        if (document.exists()) {
            return document.toObject(User.class);
        }
        return null;
    }

    /**
     * Updates a specific field of a user.
     *
     * @param user  The user to update.
     * @param field The field to update.
     * @param val   The new value for the field.
     */
    private void updateBy(User user, String field, Object val) {
        DocumentReference docRef = reference.document(user.getId());
        docRef.update(field, val);
    }

    /**
     * Updates the name of a user.
     *
     * @param user The user to update.
     */
    @Override
    public void updateByName(User user) {
        updateBy(user, "name", user.getName());
    }

    /**
     * Updates the status of a user.
     *
     * @param user The user to update.
     */
    @Override
    public void updateByStatus(User user) {
        updateBy(user, "status", user.getStatus());
    }

    /**
     * Updates the profile picture of a user.
     *
     * @param user The user to update.
     */
    @Override
    public void updateByProfilePicture(User user) {
        updateBy(user, "profilePicture", user.getProfilePicture());
    }

    /**
     * Finds users by their name.
     *
     * @param name The name to search for.
     * @return A list of users with the given name.
     * @throws ExecutionException   If the computation threw an exception.
     * @throws InterruptedException If the current thread was interrupted while
     *                              waiting.
     */
    @Override
    public List<User> findByName(String name) throws ExecutionException, InterruptedException {
        return findBy("name", name);
    }

    /**
     * Finds users by their email.
     *
     * @param email The email to search for.
     * @return A list of users with the given email.
     * @throws ExecutionException   If the computation threw an exception.
     * @throws InterruptedException If the current thread was interrupted while
     *                              waiting.
     */
    @Override
    public List<User> findByEmail(String email) throws ExecutionException, InterruptedException {
        return findBy("email", email);
    }

    /**
     * Finds a user by their exact email.
     *
     * @param email The email to search for.
     * @return The user with the given email, or null if not found.
     * @throws ExecutionException   If the computation threw an exception.
     * @throws InterruptedException If the current thread was interrupted while
     *                              waiting.
     */
    @Override
    public User findByExactEmail(String email) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = reference.whereEqualTo("email", email).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        if (documents.isEmpty()) {
            return null;
        }
        return documents.get(0).toObject(User.class);
    }

    /**
     * Adds a contact to a user's contact list.
     *
     * @param user      The user to update.
     * @param contactId The contact ID to add.
     */
    @Override
    public void addContact(User user, String contactId) {
        updateBy(user, "contacts", FieldValue.arrayUnion(contactId));
    }
  /**
     * Remove a contact from a user's contact list.
     *
     * @param user      The user to update.
     * @param contactId The contact ID to add.
     */
    @Override
    public void removeContact(User user, String contactId) {
        updateBy(user, "contacts", FieldValue.arrayRemove(contactId));
    }

    /**
     * Finds users by a specific field and value.
     *
     * @param field The field to search by.
     * @param value The value to search for.
     * @return A list of users matching the field and value.
     * @throws ExecutionException   If the computation threw an exception.
     * @throws InterruptedException If the current thread was interrupted while
     *                              waiting.
     */
    private List<User> findBy(String field, String value) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = reference.whereGreaterThan(field, value.toUpperCase())
                .whereLessThan(field, value.toLowerCase() + '\uf8ff').get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<User> users = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            users.add(document.toObject(User.class));
        }
        return users;
    }
}