package org.example.dao;


import org.example.model.User;

import java.util.List;

public interface UserDao {

    User createUser(User user);

    User getById(Long id);

    User updateUser(Long id, User user);

    List<User> findAllUsers();

    void deleteUser(Long id);

    List<User> getUsersForParams(Integer from, Integer size, String nameFilter);
}
