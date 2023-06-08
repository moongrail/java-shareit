package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperDto;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.UserParameterException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto save(UserDto userDto) {
        Optional<User> save = userStorage.save(userDto);

        if (save.isPresent()) {
            return UserMapperDto.toUserDto(save.get());
        }

        throw new UserParameterException("Ошибка создания пользователя");
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        Optional<User> update = userStorage.patch(id, userDto);

        if (update.isPresent()) {
            return UserMapperDto.toUserDto(update.get());
        }

        throw new UserNotFoundException("Пользователь не найден");
    }

    @Override
    public UserDto findById(Long id) {
        Optional<User> findById = userStorage.findById(id);

        if (findById.isPresent()) {
            return UserMapperDto.toUserDto(findById.get());
        }

        throw new UserNotFoundException("Пользователь не найден");
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }

    @Override
    public List<UserDto> findAll() {
        return UserMapperDto.toListUserDto(userStorage.findAll());
    }
}
