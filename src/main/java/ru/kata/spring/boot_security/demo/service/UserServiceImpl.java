package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.CreateUserRequest;
import ru.kata.spring.boot_security.demo.dto.UpdateUserRequest;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleDao;
import ru.kata.spring.boot_security.demo.repository.UserDao;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
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
    @Transactional
    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
    }

    @Override
    @Transactional
    public void addUser(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setAge(request.getAge());
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = roleDao.findAllById(request.getRoleIds()).stream().collect(Collectors.toSet());
            user.setRoles(roles);
        }
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
    @Transactional(readOnly = true)
    public User getUserWithRoles(Long id) {
        return userDao.findByIdWithRoles(id).orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    @Override
    public List<User> getUsers() {
        return userDao.findAllWithRoles();
    }

    @Override
    @Transactional
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
    @Transactional
    public void updateUser(UpdateUserRequest request) {
        User user = userDao.findById(request.getId()).orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setAge(request.getAge());

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = roleDao.findAllById(request.getRoleIds()).stream().collect(Collectors.toSet());
            user.setRoles(roles);
        }
        userDao.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }

    // временные пользователи и роли для теста
    @PostConstruct
    @Transactional
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
