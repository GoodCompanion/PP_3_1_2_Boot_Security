package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dto.CreateUserRequest;
import ru.kata.spring.boot_security.demo.dto.UpdateUserRequest;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {
    void addUser(User user);

    void addUser(CreateUserRequest request);

    User getUser(Long id);

    User findByUsername(String username);

    List<User> getUsers();

    void updateUser(User user);

    void updateUser(UpdateUserRequest request);

    void deleteUser(Long id);
}
