package com.amir.eventmanager.users.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(

        @NotBlank
        String login,
        @NotBlank
        String password,
        @Min(value = 18)
        Integer age
) {
}
