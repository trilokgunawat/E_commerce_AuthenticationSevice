package com.example.authenticationservice.dtos;

import com.example.authenticationservice.models.Role;
import com.example.authenticationservice.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class UserDto {
    private String email;
    private Set<Role> roles = new HashSet<>();

    public static UserDto from (User user){
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        return userDto;
    }
}
