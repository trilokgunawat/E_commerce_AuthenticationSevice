package com.example.authenticationservice.dtos;

import com.example.authenticationservice.models.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateTokenRequestDto {
    private String token;
    private Long userId;
}
