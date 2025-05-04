package com.amir.eventmanager.users.domain;

import com.amir.eventmanager.users.db.UserEntity;
import com.amir.eventmanager.users.db.UserEntityMapper;
import com.amir.eventmanager.users.db.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("User entity wasn't found by login=%s"
                        .formatted(login)));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("User wasn't found bu id=%s"
                        .formatted(userId)));
    }
}
