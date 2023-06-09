package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemClientController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(name = "${headers.user.id.name}") Long userId,
                                              @PositiveOrZero @RequestParam(name = "from", required = false,
                                                      defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", required = false,
                                                      defaultValue = "10") Integer size) {
        log.info("Get all items, userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(name = "${headers.user.id.name}") Long userId,
                                              @PathVariable Long itemId) {
        log.info("Get item by id, userId={}, itemId={}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(name = "${headers.user.id.name}") Long userId,
                                          @RequestParam(name = "requestId", required = false) Long requestId,
                                          @RequestBody @Valid ItemDto itemDto) {
        log.info("Add item, userId={}, requestId={}, itemDto={}", userId, requestId, itemDto);
        return itemClient.addItem(userId, itemDto, requestId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader(name = "${headers.user.id.name}") Long userId,
                                            @PathVariable Long itemId,
                                            @RequestBody ItemDto itemDto) {

        log.info("Update item, userId={}, itemId={}, itemDto={}", userId, itemId, itemDto);
        return itemClient.patchItem(itemId, userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long itemId) {
        log.info("Delete item, itemId={}", itemId);
        return itemClient.deleteItem(itemId);
    }


    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestParam String text,
                                                    @RequestHeader(name = "${headers.user.id.name}") Long userid,
                                                    @PositiveOrZero @RequestParam(name = "from",
                                                            required = false, defaultValue = "0")
                                                    Integer from,
                                                    @Positive @RequestParam(name = "size",
                                                            required = false, defaultValue = "10")
                                                    Integer size) {
        log.info("Search items by text, text={}, from={}, size={}", text, from, size);
        return itemClient.searchItemsByText(text, userid, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(name = "${headers.user.id.name}") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid CommentRequestDto commentRequestDto) {
        log.info("Add comment, userId={}, itemId={}, commentRequestDto={}", userId, itemId, commentRequestDto);
        return itemClient.addComment(userId, itemId, commentRequestDto);
    }
}
