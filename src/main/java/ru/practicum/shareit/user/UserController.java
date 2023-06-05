package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(path = "/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable("userId") Long userId) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findById(userId));
    }

    @GetMapping()
    public ResponseEntity<List<UserDto>> getAllUser() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findAll());
    }

    @PostMapping()
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(userDto);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.save(userDto));
    }

    @PatchMapping(path = "/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("userId") Long userId,
                                              @RequestBody UserDto userDto) {

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.update(userId, userDto));
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long id) {
        userService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
