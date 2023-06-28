package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPost;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @GetMapping()
    public ResponseEntity<List<ItemRequestDto>> getItemRequests(@RequestHeader(value = HEADER_USER_ID) Long userId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRequestService.getItemRequests(userId));

    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequest(@RequestHeader(value = HEADER_USER_ID) Long userId,
                                                         Long requestId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRequestService.getItemRequest(userId, requestId));

    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllItemRequests(@RequestHeader(value = HEADER_USER_ID) Long userId,
                                                                @RequestParam(name = "from",defaultValue = "0") Long from,
                                                                @RequestParam(name = "size") Long size) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRequestService.getAllItemRequests(userId, from, size));

    }

    @PostMapping()
    public ResponseEntity<ItemRequestDto> addItemRequest(@RequestHeader(value = HEADER_USER_ID) Long userId,
                                                               @RequestBody @Valid ItemRequestPost itemRequestPost) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRequestService.addItemRequest(userId, itemRequestPost));
    }
}
