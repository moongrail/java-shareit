package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.UserUniqueEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    private UserService userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
        userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@mail.com")
                .build();

        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@mail.com")
                .build();
    }

    @Test
    void save_whenInvoked_thenUserSave() {
        when(userRepository.save(any())).thenReturn(user);

        UserDto userSave = userService.save(userDto);

        assertEquals(user.getEmail(), userSave.getEmail());
        assertEquals(user.getName(), userSave.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void save_whenInvokedWIthExistEmail_thenThrowsUserUniqueEmailException() {
        when(userRepository.existsUserByEmailIs(user.getEmail())).thenThrow(UserUniqueEmailException.class);

        assertThrows(UserUniqueEmailException.class, () -> userService.save(userDto));

        verify(userRepository, never()).save(user);
        verify(userRepository, times(1)).existsUserByEmailIs(user.getEmail());
    }

    @Test
    void update_whenUpdateNameAndEmail_thenUpdated() {
        User userUpdate = User.builder()
                .id(1L)
                .name("nameUpdate")
                .email("emailUpdate@mail.com")
                .build();

        when(userRepository.save(any())).thenReturn(userUpdate);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserDto userSave = userService.update(user.getId(), UserMapperDto.toUserDto(userUpdate));

        assertEquals(userUpdate.getEmail(), userSave.getEmail());
        assertEquals(userUpdate.getName(), userSave.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void update_whenUpdateUserNotExist_thenThrowsUserNotFoundException() {
        User userUpdate = User.builder()
                .id(1L)
                .name("nameUpdate")
                .email("emailUpdate@mail.com")
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.empty());
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> userService.update(userUpdate.getId(), UserMapperDto.toUserDto(userUpdate)));

        assertEquals("Пользователь не найден", userNotFoundException.getMessage());
        verify(userRepository, times(0)).save(user);
        verify(userRepository, times(1)).findById(userUpdate.getId());
    }

    @Test
    void update_whenUpdateUserEmailExist_thenThrowsUserUniqueEmailException() {
        User userUpdate = User.builder()
                .id(1L)
                .name("nameUpdate")
                .email("email@mail.com")
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.existsUserByEmailIs(userUpdate.getEmail())).thenThrow(UserUniqueEmailException.class);

        assertThrows(UserUniqueEmailException.class,
                () -> userService.update(userUpdate.getId(), UserMapperDto.toUserDto(userUpdate)));
        verify(userRepository, times(0)).save(user);
        verify(userRepository, times(1)).existsUserByEmailIs(userUpdate.getEmail());
    }

    @Test
    void findById_whenInvoked_thenUserExist() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserDto userDto = userService.findById(user.getId());

        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findById_whenUserNotExist_thenThrowUserNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> userService.findById(user.getId()));

        assertEquals("Пользователь не найден", userNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void delete_whenInvoked_thenDeleted() {
        when(userRepository.existsUserByIdIs(any())).thenReturn(true);

        userService.delete(user.getId());

        verify(userRepository, times(1)).existsUserByIdIs(user.getId());
        verify(userRepository, times(1)).deleteUserByIdIs(user.getId());
    }

    @Test
    void delete_whenUserNotExist_thenThrowUserNotFound() {
        when(userRepository.existsUserByIdIs(eq(user.getId()))).thenReturn(false);

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> userService.delete(user.getId()));

        assertEquals("Пользователь не найден", userNotFoundException.getMessage());
        verify(userRepository, times(1)).existsUserByIdIs(user.getId());
        verify(userRepository, times(0)).deleteUserByIdIs(user.getId());
    }

    @Test
    void findAll_whenInvoked_thenListHaveOneUser() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> userDtoList = userService.findAll();

        assertEquals(1, userDtoList.size());
    }
    @Test
    void findAll_whenInvoked_thenListEmpty() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> userDtoList = userService.findAll();

        assertEquals(0, userDtoList.size());
    }
}