package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemService.findAll());
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemService.findById(itemId));
    }

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                           @RequestBody @Valid ItemDto itemDto,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(itemDto);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemService.save(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody @Valid ItemDto itemDto,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(itemDto);
        }

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
    public ResponseEntity<List<ItemDto>> searchItemsByText(@RequestParam String text) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemService.findByText(text));
    }
}
