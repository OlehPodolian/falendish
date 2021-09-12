package main.java.alex.falendish.dao;

import main.java.alex.falendish.model.User;

import java.util.Optional;

public interface UserDAO {

    Optional<User> findById(Long userId);

    Optional<User> findByUsername(String username);

    User create(User user);

    User update(Long userId, User user);

    void delete(Long userId);

}
