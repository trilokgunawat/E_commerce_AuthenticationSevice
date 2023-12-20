package com.example.authenticationservice.controller;

import com.example.authenticationservice.Exception.UserAlreadyExistsException;
import com.example.authenticationservice.Exception.UserDoesNotExistException;
import com.example.authenticationservice.dtos.*;
import com.example.authenticationservice.models.Session;
import com.example.authenticationservice.models.SessionStatus;
import com.example.authenticationservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.authenticationservice.models.SessionStatus;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto request) throws UserAlreadyExistsException {
        UserDto userDto = authService.signUp(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request) throws UserDoesNotExistException {
        return authService.logIn(request.getEmail(), request.getPassword());
    }
    @PostMapping("/validate")
    public ResponseEntity<ValidateTokenResponseDto> validateToken(@RequestBody ValidateTokenRequestDto request){
        Optional<UserDto> userDto = authService.validate(request.getToken(), request.getUserId());
        if(userDto.isEmpty()){
            ValidateTokenResponseDto responseDto = new ValidateTokenResponseDto();
            responseDto.setSessionStatus(SessionStatus.INVALID);

            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }
        ValidateTokenResponseDto responseDto = new ValidateTokenResponseDto();
        responseDto.setUserDto(userDto.get());
        responseDto.setSessionStatus(SessionStatus.ACTIVE);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
