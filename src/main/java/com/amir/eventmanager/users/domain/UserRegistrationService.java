package com.amir.eventmanager.users.domain;

import com.amir.eventmanager.users.api.SignUpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(SignUpRequest signUpRequest) {
        if (userService.isUserExistByLogin(signUpRequest.login())) {
            throw new IllegalArgumentException("User with such login already exist");
        }
        String hashedPass = passwordEncoder.encode(signUpRequest.password());
        User user = new User(
                null,
                signUpRequest.login(),
                signUpRequest.age(),
                UserRole.USER,
                hashedPass);
        return userService.saveUser(user);
    }
}
