package com.example.authenticationservice.service;


import com.example.authenticationservice.Exception.UserAlreadyExistsException;
import com.example.authenticationservice.Exception.UserDoesNotExistException;
import com.example.authenticationservice.dtos.UserDto;
import com.example.authenticationservice.models.Session;
import com.example.authenticationservice.models.SessionStatus;
import com.example.authenticationservice.models.User;
import com.example.authenticationservice.repositories.SessionRepository;
import com.example.authenticationservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.oauth2.resourceserver.OpaqueTokenDsl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import javax.xml.crypto.Data;
import java.util.*;

import io.jsonwebtoken.Jwts;
@Service
public class AuthService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private final SessionRepository sessionRepository;

    public AuthService(UserRepository userRepository,
                       SessionRepository sessionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionRepository = sessionRepository;
    }

    public UserDto signUp(String email, String password) throws UserAlreadyExistsException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(!userOptional.isEmpty()){
            throw new UserAlreadyExistsException("User already exists with this username");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        User savedUser = userRepository.save(user);
        return UserDto.from(savedUser);
    }
    public ResponseEntity<UserDto> logIn(String email, String password) throws UserDoesNotExistException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            throw new UserDoesNotExistException("No user exist with this " + email );
        }
        User user = userOptional.get();

        if(!passwordEncoder.matches(password, user.getPassword())){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Generate token
//        String token = RandomStringUtils.randomAscii(10);
//        token = "a" + token;
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("userId",  user.getId());
        claimsMap.put("email", user.getEmail());


        String token = Jwts.builder().claims(claimsMap).compact();


        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add("Auth_Token", token);

        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setSessionStatus(SessionStatus.ACTIVE);
        sessionRepository.save(session);

        UserDto userDto = UserDto.from(user);
        ResponseEntity<UserDto> response = new ResponseEntity<>(
                userDto,
                headers,
                HttpStatus.OK
        );
        return response;
    }


    public Optional<UserDto> validate(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndAndUser_Id(token, userId);

        if(sessionOptional.isEmpty()){
            return Optional.empty();
        }
        Session session = sessionOptional.get();

        if(!session.getSessionStatus().equals(SessionStatus.ACTIVE)){
            return Optional.empty();
        }
//        if(!session.getExpiringAt() > new Date()){
//            return  SessionStatus.EXPIRED;
//        }
        User user =  userRepository.findById(userId).get();
        UserDto userDto = UserDto.from(user);

        return Optional.of(userDto);
    }
    public ResponseEntity<Void> logout(String token, Long userId){
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndAndUser_Id(token, userId);
        if(sessionOptional.isEmpty()){
            return null;
        }
        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.EXPIRED);

        sessionRepository.save(session);
        return ResponseEntity.ok().build();
    }
}
