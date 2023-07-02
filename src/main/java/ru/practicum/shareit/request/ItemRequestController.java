package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public ResponseEntity<List<ItemRequestDto>> getItemRequests(@RequestHeader(value = HEADER_USER_ID) Long requesterId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRequestService.getItemRequests(requesterId));

    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequest(@RequestHeader(value = HEADER_USER_ID) Long requesterId,
                                                         @PathVariable Long requestId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRequestService.getItemRequest(requesterId, requestId));

    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllItemRequests(@RequestHeader(value = HEADER_USER_ID) Long requesterId,
                                                                   @RequestParam(name = "from", required = false)
                                                                   Integer from,
                                                                   @RequestParam(name = "size", required = false)
                                                                   Integer size) {

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRequestService.getAllItemRequests(requesterId, from, size));

    }

    @PostMapping()
    public ResponseEntity<ItemRequestDto> addItemRequest(@RequestHeader(value = HEADER_USER_ID) Long requesterId,
                                                         @RequestBody @Valid ItemRequestPost itemRequestPost,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRequestService.addItemRequest(requesterId, itemRequestPost));
    }
}
