package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto);
    UserDto update(Long id, UserDto userDto);
    UserDto findById(Long id);
    void delete(Long id);
    List<UserDto> findAll();
}
