package com.amir.eventmanager.users.api;

import com.amir.eventmanager.users.domain.UserRole;

public record UserDto(
        Long id,
        String login,
        Integer age,
        UserRole role
) {

}
