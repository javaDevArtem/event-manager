package com.amir.eventmanager.security;

import com.amir.eventmanager.users.domain.User;
import com.amir.eventmanager.users.domain.UserRole;
import com.amir.eventmanager.users.domain.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserInitializer {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserInitializer(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initUsers() {
        createUserIfNotExist("admin", "admin", UserRole.ADMIN);
        createUserIfNotExist("user", "user", UserRole.USER);
    }

    private void createUserIfNotExist(
            String login,
            String password,
            UserRole role
    ) {
        if (userService.isUserExistByLogin(login)) {
            return;
        }
        String hashedPass = passwordEncoder.encode(password);
        User user = new User(
                null,
                login,
                21,
                role,
                hashedPass);
        userService.saveUser(user);
    }
}
