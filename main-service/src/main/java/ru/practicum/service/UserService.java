package ru.practicum.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.UserDto;

import java.util.List;

public interface UserService {

    ResponseEntity<UserDto> updateUser(UserDto user, Long id);

    List<UserDto> getAllUsers();

    UserDto saveUser(UserDto userDto);

    List<UserDto> getUser(List<Long> ids);

    List<UserDto> getUser(Long from, Long size);

    UserDto getUser(Long id);

    void deleteUser(Long id);

}
