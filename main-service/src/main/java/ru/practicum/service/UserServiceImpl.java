package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.UserDto;
import ru.practicum.handler.exception.ConflictException;
import ru.practicum.handler.exception.ObjectNotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Transactional
    public ResponseEntity<UserDto> updateUser(UserDto userDto, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        return new ResponseEntity<>(UserMapper.toUserDto(userRepository.save(user)), HttpStatus.OK);
    }


    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.mapToUserDto(users);
    }

    @Transactional
    @Override
    public UserDto saveUser(UserDto userDto) {
        String name = userDto.getName();
        if (userRepository.findByName(name).isEmpty()) {
            User user = userRepository.save(UserMapper.toDtoUser(userDto));
            return UserMapper.toUserDto(user);
        } else {
            throw new ConflictException("Пользователь с именем " + name + " уже существует.");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        return UserMapper.toUserDto(user);
    }

    @Transactional(readOnly = true) ///
    @Override
    public List<UserDto> getUser(Long from, Long size) {
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        return userRepository.findAll(page).stream()
                .map(UserMapper::toUserDto).collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<UserDto> getUser(List<Long> ids) {  ///
        List<UserDto> userDtos = new ArrayList<>();
        for (long id : ids) {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                userDtos.add(UserMapper.toUserDto(user.get()));
            } else {
                continue;
            }
        }
        return userDtos;
    }

    @Transactional ///
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


}

