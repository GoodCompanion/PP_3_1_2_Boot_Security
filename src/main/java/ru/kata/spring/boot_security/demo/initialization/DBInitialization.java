package ru.kata.spring.boot_security.demo.initialization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleDao;
import ru.kata.spring.boot_security.demo.repository.UserDao;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import java.util.Set;

@Component
public class DBInitialization {
    private final UserDao userDao;
    private final RoleDao roleDao;
    private final UserService userService;

    @Autowired
    public DBInitialization(UserDao userDao, RoleDao roleDao, UserService userService) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.userService = userService;
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
            userService.addUser(admin);
            userService.addUser(user);
        }
    }
}
