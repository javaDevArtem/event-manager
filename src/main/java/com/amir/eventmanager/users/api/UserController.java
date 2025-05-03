package com.amir.eventmanager.users.api;

import com.amir.eventmanager.users.domain.User;
import com.amir.eventmanager.users.domain.UserRegistrationService;
import com.amir.eventmanager.users.domain.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final UserRegistrationService registrationService;
    private final AuthenticationService authenticationService;
    private final UserDtoMapper dtoMapper;

    public UserController(UserService userService,
                          UserRegistrationService registrationService,
                          AuthenticationService authenticationService,
                          UserDtoMapper dtoMapper) {
        this.userService = userService;
        this.registrationService = registrationService;
        this.authenticationService = authenticationService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid SignUpRequest signUpRequest) {
        log.info("Get request for sign-up: login={}", signUpRequest.login());
        User user = registrationService.registerUser(signUpRequest);
        return ResponseEntity.status(201)
                .body(dtoMapper.convertDomainUser(user));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> authenticate(
            @RequestBody @Valid SignInRequest signInRequest
    ) {
        log.info("Get request for authenticate user: login={}", signInRequest.login());
        String token = authenticationService.authenticateUser(signInRequest);
        return ResponseEntity.ok(new JwtTokenResponse(token));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserInfo(
            @PathVariable Long userId
    ) {
        log.info("Get request for get user info: userId={}", userId);
        User userById = userService.getUserById(userId);
        return ResponseEntity.ok(dtoMapper.convertDomainUser(userById));
    }

}
