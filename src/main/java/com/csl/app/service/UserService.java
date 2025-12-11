package com.csl.app.service;

import com.csl.app.model.User;
import java.util.List;

public interface UserService {
    User saveUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    User getUserByEmail(String email);
}