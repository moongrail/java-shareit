package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.UserParameterException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {UserController.class})
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;
    private UserDto testUser;

    @BeforeEach
    void setUp() {
        testUser = getTestUser();
    }

    @Test
    @SneakyThrows
    void getUser_whenInvokeCorrect_thenHaveUserStatusOk() {
        when(userService.findById(1L)).thenReturn(testUser);
        mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(testUser.getName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getUser_whenInvokeIncorrectId_thenStatusNotFound() {
        when(userService.findById(0L))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/users/{userId}", 0L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void getAllUser_whenInvoke_thenListTwoUserStatusOk() {
        when(userService.findAll()).thenReturn(getTestUsersTwoUsers());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(content().json(objectMapper.writeValueAsString(getTestUsersTwoUsers())))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void testGetAllUser_whenInvoke_thenListEmptyStatusOk() {
        when(userService.findAll()).thenReturn(getTestUsersEmptyUser());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(content().json(objectMapper.writeValueAsString(getTestUsersEmptyUser())))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void createUser_whenInvokeCorrect_thenCreatedStatusOk() {
        UserDto userDto = UserDto.builder()
                .name("test")
                .email("test@email.com")
                .build();

        when(userService.save(any())).thenReturn(testUser);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void createUser_whenInvokeIncorrectName_thenCreatedStatusBadRequest() {
        UserDto userDto = UserDto.builder()
                .email("test@email.com")
                .build();

        when(userService.save(any())).thenThrow(UserParameterException.class);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void createUser_whenInvokeIncorrectEmail_thenCreatedStatusBadRequest() {
        UserDto userDto = UserDto.builder()
                .name("test")
                .email("testemail.com")
                .build();

        when(userService.save(userDto)).thenThrow(UserParameterException.class);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void createUser_whenInvokeIncorrectEmptyBody_thenCreatedStatusBadRequest() {
        UserDto userDto = UserDto.builder()
                .build();

        when(userService.save(userDto)).thenThrow(UserParameterException.class);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void updateUser_whenFullUpdateCorrect_thenStatusIsOk() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("testUpdate")
                .email("testUpdate@email.com")
                .build();

        when(userService.update(eq(1L), eq(userDto))).thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void updateUser_whenUpdateIncorrectId_thenStatusIsNotFound() {
        UserDto userDto = UserDto.builder()
                .id(100L)
                .name("testUpdate")
                .email("testUpdate@email.com")
                .build();

        when(userService.update(eq(100L), eq(userDto))).thenThrow(UserNotFoundException.class);

        mockMvc.perform(patch("/users/{userId}", 100L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void updateUser_whenUpdateIncorrectEmail_thenStatusIsBadRequest() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("testUpdate")
                .email("testUpdateemail.com")
                .build();

        when(userService.update(eq(1L), eq(userDto))).thenThrow(UserParameterException.class);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void updateUser_whenUpdatePartEmail_thenStatusIsOk() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("test")
                .email("testUpdate@email.com")
                .build();

        when(userService.update(eq(1L), eq(userDto))).thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void updateUser_whenUpdatePartName_thenStatusIsOk() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("testUpdate")
                .email("test@email.com")
                .build();

        when(userService.update(eq(1L), eq(userDto))).thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void deleteUser_whenInvoke_thenStatusIsOk() {
        mockMvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void deleteUser_whenInvokeNotExist_thenStatusIsNotFound() {
        doThrow(UserNotFoundException.class).when(userService).delete(0L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", 0L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    private static List<UserDto> getTestUsersEmptyUser() {
        return List.of(ru.practicum.shareit.user.dto.UserDto.builder().build());
    }

    private static List<UserDto> getTestUsersTwoUsers() {
        return List.of(ru.practicum.shareit.user.dto.UserDto.builder()
                        .id(2L)
                        .name("test2")
                        .email("test2@email.com")
                        .build(),
                ru.practicum.shareit.user.dto.UserDto.builder()
                        .id(3L)
                        .name("test3")
                        .email("test3@email.com")
                        .build());
    }

    private static UserDto getTestUser() {
        return UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@email.com")
                .build();
    }
}