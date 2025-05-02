package com.amir.eventmanager.users.domain;

import com.amir.eventmanager.users.db.UserEntity;
import com.amir.eventmanager.users.db.UserEntityMapper;
import com.amir.eventmanager.users.db.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserEntityMapper entityMapper;

    public UserService(UserRepository userRepository, UserEntityMapper entityMapper) {
        this.userRepository = userRepository;
        this.entityMapper = entityMapper;
    }

    public boolean isUserExistByLogin(String login) {
        return userRepository.findByLogin(login).isPresent();
    }

    public User saveUser(User user) {
        log.info("Save user: user={}", user);
        UserEntity entity = entityMapper.toEntity(user);
        UserEntity savedUser = userRepository.save(entity);
        return entityMapper.toDomain(savedUser);
    }
}
