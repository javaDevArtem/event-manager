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
    private final UserEntityMapper userEntityMapper;

    public UserService(UserRepository userRepository, UserEntityMapper userEntityMapper) {
        this.userRepository = userRepository;
        this.userEntityMapper = userEntityMapper;
    }

    public User saveUser(User user) {
        log.info("Save user: user={}", user);
        UserEntity entity = userEntityMapper.toEntity(user);
        UserEntity savedUser = userRepository.save(entity);
        return userEntityMapper.toDomain(savedUser);
    }

    public boolean isUserExistsByLogin(String login) {
        return userRepository.findByLogin(login)
                .isPresent();
    }

    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(userEntityMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("User entity wasn't found by login=%s"
                        .formatted(login)));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userEntityMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("User wasn't found bu id=%s"
                        .formatted(userId)));
    }
}
