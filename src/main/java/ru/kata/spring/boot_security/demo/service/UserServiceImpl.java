package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleDao;
import ru.kata.spring.boot_security.demo.repository.UserDao;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
    }

    @Override
    public User getUser(Long id) {
        return userDao.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username).orElse(null);
    }

    @Override
    public List<User> getUsers() {
        return userDao.findAllWithRoles();
    }

    @Override
    public void updateUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            if (!user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        } else {
            User existingUser = getUser(user.getId());
            user.setPassword(existingUser.getPassword());
        }
        userDao.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }

    // временные пользователи и роли для теста
    @PostConstruct
    public void init() {
        if (userDao.count() == 0) {
            Role adminRole = new Role("ROLE_ADMIN");
            Role userRole = new Role("ROLE_USER");

            if (roleDao.count() == 0) {
                roleDao.save(adminRole);
                roleDao.save(userRole);
            }

            User admin = new User("admin", "admin", "Владимир", "Калинин", 25, Set.of(adminRole, userRole));
            User user = new User("user", "user", "Эрик", "Цой", 24, Set.of(userRole));
            addUser(admin);
            addUser(user);
        }
    }
}
