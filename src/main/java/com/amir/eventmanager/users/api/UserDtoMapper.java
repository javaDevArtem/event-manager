package com.amir.eventmanager.users.api;

import com.amir.eventmanager.users.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public UserDto convertDomainUser(User user) {
        return new UserDto(
                user.id(),
                user.login(),
                user.age(),
                user.role()
        );
    }

}
