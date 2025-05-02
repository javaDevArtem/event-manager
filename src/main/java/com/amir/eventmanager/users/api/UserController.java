package com.amir.eventmanager.users.api;

import com.amir.eventmanager.security.JwtTokenManager;
import com.amir.eventmanager.users.domain.User;
import com.amir.eventmanager.users.domain.UserRegistrationService;
import com.amir.eventmanager.users.domain.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final UserRegistrationService registrationService;
    private final JwtTokenManager jwtTokenManager;

    public UserController(UserService userService, UserRegistrationService registrationService, JwtTokenManager jwtTokenManager) {
        this.userService = userService;
        this.registrationService = registrationService;
        this.jwtTokenManager = jwtTokenManager;
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid SignUpRequest signUpRequest) {
        log.info("Get request for sign-up: login={}", signUpRequest.login());
        User user = registrationService.registerUser(signUpRequest);
        
        var token = jwtTokenManager.generateToken(user);
        return ResponseEntity.status(201)
                .body(convertDomainUser(user));
    }

    private UserDto convertDomainUser(User user) {
        return new UserDto(
                user.id(),
                user.login(),
                user.age(),
                user.role()
        );
    }
}
