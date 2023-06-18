package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserUniqueEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperDto;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.UserParameterException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.user.dto.UserMapperDto.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto save(UserDto userDto) {
        User user = fromUserDto(userDto.getId(), userDto);

        if (userRepository.existsUserByEmailIs(userDto.getEmail())) {
            userRepository.save(user);
            throw new UserUniqueEmailException("Пользователь с таким email уже существует");
        }

        User save = userRepository.save(user);
        log.info("Пользователь сохранен: " + save);

        return toUserDto(save);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        Optional<User> byIdUser = userRepository.findById(id);

        if (byIdUser.isPresent()) {
            User userForPatch = byIdUser.get();

            if (userDto.getEmail() != null) {
                if (userRepository.existsUserByEmailIs(userDto.getEmail()) &&
                        !userForPatch.getEmail().equals(userDto.getEmail())) {
                    throw new UserUniqueEmailException("Пользователь с таким email уже существует");
                }

                log.info("Пользователь {} обновил поле email с {} на {}", userForPatch.getId(),
                        userForPatch.getEmail(), userDto.getEmail());

                userForPatch.setEmail(userDto.getEmail());
            }

            if (userDto.getName() != null) {
                log.info("Пользователь {} обновил поле name с {} на {}", userForPatch.getId(),
                        userForPatch.getName(), userDto.getName());

                userForPatch.setName(userDto.getName());
            }

            User patch = userRepository.save(userForPatch);
            log.info("Пользователь обновлён: " + patch);

            return toUserDto(patch);
        }

        throw new UserNotFoundException("Пользователь не найден");
    }

    @Override
    public UserDto findById(Long id) {
        Optional<User> findById = userRepository.findById(id);

        if (findById.isPresent()) {
            return toUserDto(findById.get());
        }

        throw new UserNotFoundException("Пользователь не найден");
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsUserByIdIs(id)) {
            throw new UserNotFoundException("Пользователь не найден");
        }

        userRepository.deleteUserByIdIs(id);
    }

    @Override
    public List<UserDto> findAll() {
        return toListUserDto(userRepository.findAll());
    }
}
