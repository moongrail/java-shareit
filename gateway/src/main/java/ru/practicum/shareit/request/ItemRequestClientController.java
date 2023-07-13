package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestPost;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestClientController {
    private final ItemRequestClient itemRequestClient;


    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader(value = "${headers.user.id.name}") Long requesterId) {
        log.info("Get booking with userId={}", requesterId);
        return itemRequestClient.getItemRequests(requesterId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(value = "${headers.user.id.name}") Long requesterId,
                                                     @Positive @PathVariable Long requestId) {

        log.info("Get booking with requestId {}, userId={}", requestId, requesterId);
        return itemRequestClient.getItemRequestById(requesterId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(value = "${headers.user.id.name}") Long requesterId,
                                                     @RequestParam(name = "from", required = false, defaultValue = "0")
                                                     @PositiveOrZero Integer from,
                                                     @Positive @RequestParam(name = "size", required = false,
                                                             defaultValue = "10")
                                                     Integer size) {
        log.info("Get all booking with userId={}, from={}, size={}", requesterId, from, size);
        return itemRequestClient.getAllItemRequests(requesterId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(value = "${headers.user.id.name}") Long requesterId,
                                                 @RequestBody @Valid ItemRequestPost itemRequestPost) {
        log.info("Add booking with userId={}, description={}", requesterId, itemRequestPost.getDescription());
        return itemRequestClient.addItemRequest(requesterId, itemRequestPost);
    }
}
