package com.bioauth.user;

import com.bioauth.exceptions.UserException;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for user data access operations
 */
public interface IUserRepository {
    void save(User user) throws UserException;
    void update(User user) throws UserException;
    void delete(String userId) throws UserException;
    Optional<User> findById(String userId) throws UserException;
    Optional<User> findByUsername(String username) throws UserException;
    Map<String, User> findAll() throws UserException;
    boolean exists(String userId) throws UserException;
    int count() throws UserException;
}
