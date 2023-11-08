package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dao.UserDao;
import org.example.model.User;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    public User createUser(User user) {
        return userDao.createUser(user);
    }

    public User getById(Long id) {
        return userDao.getById(id);
    }

    public List<User> findAllUsers() {
        return userDao.findAllUsers();
    }

    public User updateUser(Long id, User user) {
        return userDao.updateUser(id, user);
    }


    public void deleteUser(Long id) {
        userDao.deleteUser(id);
    }

    public List<User> getUsersForParams(Integer from, Integer size, String nameFilter) {
        return userDao.getUsersForParams(from, size, nameFilter);
    }
}
