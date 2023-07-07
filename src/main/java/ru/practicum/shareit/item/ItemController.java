package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.dto.CommentRequestDto;
import ru.practicum.shareit.comments.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@PropertySource("classpath:headers.properties")
@PropertySource("classpath:application.properties")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllItems(@RequestHeader(name = "${headers.user.id.name}") Long userId,
                                                            @RequestParam(name = "from", required = false) Integer from,
                                                            @RequestParam(name = "size", required = false) Integer size) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemService.findAllItemByUserId(userId, from, size));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItemById(@RequestHeader(name = "${headers.user.id.name}") Long userId,
                                                       @PathVariable Long itemId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemService.findById(itemId, userId));
    }

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(name = "${headers.user.id.name}") Long userId,
                                           @RequestParam(name = "requestId", required = false) Long requestId,
                                           @RequestBody @Valid ItemDto itemDto,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(itemDto);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemService.save(userId, itemDto, requestId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(name = "${headers.user.id.name}") Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemService.patch(itemId, userId, itemDto));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> deleteItem(@PathVariable Long itemId) {
        itemService.delete(itemId);
        return ResponseEntity.ok().body("Удалено.");
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItemsByText(@RequestParam String text,
                                                           @RequestParam(name = "from", required = false)
                                                           Integer from,
                                                           @RequestParam(name = "size", required = false)
                                                           Integer size) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemService.findByText(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> addComment(
            @RequestHeader(name = "${headers.user.id.name}") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentRequestDto commentRequestDto
    ) {
        return ResponseEntity
                .ok()
                .body(itemService.addComment(userId, itemId, commentRequestDto));
    }
}
