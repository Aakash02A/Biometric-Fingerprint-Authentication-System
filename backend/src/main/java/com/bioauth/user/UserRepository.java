package com.bioauth.user;

import com.bioauth.exceptions.UserException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of UserRepository
 * Can be replaced with database implementation
 */
public class UserRepository implements IUserRepository {
    private final Map<String, User> userStore = new ConcurrentHashMap<>();

    @Override
    public void save(User user) throws UserException {
        if (user == null) {
            throw new UserException("User cannot be null");
        }
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            throw new UserException("User ID cannot be empty");
        }
        if (userStore.containsKey(user.getUserId())) {
            throw new UserException("User with ID " + user.getUserId() + " already exists");
        }
        userStore.put(user.getUserId(), user);
    }

    @Override
    public void update(User user) throws UserException {
        if (user == null) {
            throw new UserException("User cannot be null");
        }
        if (!userStore.containsKey(user.getUserId())) {
            throw new UserException("User with ID " + user.getUserId() + " not found");
        }
        userStore.put(user.getUserId(), user);
    }

    @Override
    public void delete(String userId) throws UserException {
        if (userId == null || userId.isEmpty()) {
            throw new UserException("User ID cannot be empty");
        }
        if (!userStore.containsKey(userId)) {
            throw new UserException("User with ID " + userId + " not found");
        }
        userStore.remove(userId);
    }

    @Override
    public Optional<User> findById(String userId) throws UserException {
        if (userId == null || userId.isEmpty()) {
            throw new UserException("User ID cannot be empty");
        }
        return Optional.ofNullable(userStore.get(userId));
    }

    @Override
    public Optional<User> findByUsername(String username) throws UserException {
        if (username == null || username.isEmpty()) {
            throw new UserException("Username cannot be empty");
        }
        return userStore.values().stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public Map<String, User> findAll() throws UserException {
        return new ConcurrentHashMap<>(userStore);
    }

    @Override
    public boolean exists(String userId) throws UserException {
        if (userId == null || userId.isEmpty()) {
            throw new UserException("User ID cannot be empty");
        }
        return userStore.containsKey(userId);
    }

    @Override
    public int count() throws UserException {
        return userStore.size();
    }

    /**
     * Clears all users (for testing purposes)
     */
    public void clear() {
        userStore.clear();
    }
}
