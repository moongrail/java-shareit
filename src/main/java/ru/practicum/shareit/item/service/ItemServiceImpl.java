package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.comments.dto.CommentMapperDto;
import ru.practicum.shareit.comments.dto.CommentRequestDto;
import ru.practicum.shareit.comments.dto.CommentResponseDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.comments.repositories.CommentRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.practicum.shareit.booking.dto.BookingMapperDto.bookingItemResponseDto;
import static ru.practicum.shareit.comments.dto.CommentMapperDto.toListComment;
import static ru.practicum.shareit.item.dto.ItemMapperDto.*;
import static ru.practicum.shareit.util.PaginationUtil.getPaginationWithoutSort;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public ItemDto save(Long userId, ItemDto itemDto, Long requestId) {
        if (userId == null) {
            throw new ItemNotHeaderUserId("Заголовок айди юзера не найден");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (requestId != null) {
            itemDto.setRequestId(requestId);
        }

        itemDto.setOwner(user);
        Item item = fromItemDto(itemDto);

        Item save = itemRepository.save(item);

        return toItemDto(save);
    }

    @Override
    @Transactional
    public ItemDto patch(Long itemId, Long owner, ItemDto itemDto) {
        if (!itemRepository.findById(itemId).get().getOwner().getId().equals(owner)) {
            throw new ItemNotFoundException("Вещь не найдена у Юзера");
        }

        Optional<Item> byId = itemRepository.findByIdFull(itemId);

        if (byId.isPresent()) {
            Item itemForUpdate = byId.get();

            if (itemDto.getName() != null) {
                itemForUpdate.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                itemForUpdate.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                itemForUpdate.setAvailable(itemDto.getAvailable());
            }
            if (itemDto.getRequestId() != null) {
                itemForUpdate.setRequestId(itemDto.getRequestId());
            }

            Item save = itemRepository.save(itemForUpdate);

            return toItemDto(save);
        }

        throw new ItemParameterException("Ошибка обновления");
    }

    @Override
    public ItemResponseDto findById(Long itemId, Long userId) {
        Item item = itemRepository.findByIdFull(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        List<CommentResponseDto> comments = toListComment(commentRepository.getCommentsByItem_idOrderByCreatedDesc(itemId));
        ItemResponseDto itemResponseDto = toItemResponseDto(item, null, null, comments);

        if (!item.getOwner().getId().equals(userId)) {
            return itemResponseDto;
        }

        List<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(DESC, "end"));
        List<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "end"));

        if (lastBooking.isEmpty() && !nextBooking.isEmpty()) {
            itemResponseDto.setLastBooking(bookingItemResponseDto(nextBooking.get(0)));
            itemResponseDto.setNextBooking(null);
        } else if (!lastBooking.isEmpty() && !nextBooking.isEmpty()) {
            itemResponseDto.setLastBooking(bookingItemResponseDto(lastBooking.get(0)));
            itemResponseDto.setNextBooking(bookingItemResponseDto(nextBooking.get(0)));
        }

        return itemResponseDto;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException("Вещь не найдена");
        }

        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemResponseDto> findAllItemByUserId(Long userId, Integer from, Integer size) {
        List<ItemResponseDto> responseDtoList = new ArrayList<>();
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Pageable paginationWithoutSort = getPaginationWithoutSort(from, size);
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(owner.getId(), paginationWithoutSort)
                .stream()
                .collect(Collectors.toList());

        for (Item item : items) {
            List<CommentResponseDto> comments = CommentMapperDto
                    .toListComment(commentRepository.getCommentsByItem_idOrderByCreatedDesc(item.getId()));
            responseDtoList.add(toItemResponseDto(item,
                    bookingRepository.findFirstByItem_idAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now()),
                    bookingRepository.findFirstByItem_idAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now()),
                    comments));
        }

        return responseDtoList;
    }

    @Override
    public List<ItemDto> findByText(String text, Integer from, Integer size) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }

        Pageable paginationWithoutSort = getPaginationWithoutSort(from, size);

        return toListItemDto(itemRepository.searchPage(text, paginationWithoutSort)
                .stream()
                .collect(Collectors.toList()));
    }

    @Override
    public CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findByIdFull(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        List<Booking> bookings = bookingRepository.findByItem_IdAndBooker_IdOrderByStartDesc(itemId, userId);

        if (!bookings.isEmpty()) {
            if (bookings.stream().anyMatch(booking ->
                    (!BookingStatus.REJECTED.equals(booking.getStatus())
                            && !BookingStatus.WAITING.equals(booking.getStatus())) &&
                            !booking.getStart().isAfter(LocalDateTime.now()))) {
                Comment comment = new Comment();
                comment.setAuthor(user);
                comment.setItem(item);
                comment.setCreated(LocalDateTime.ofInstant(LocalDateTime.now()
                        .toInstant(ZoneOffset.UTC), ZoneId.systemDefault()));
                comment.setText(commentRequestDto.getText());
                comment = commentRepository.save(comment);
                return CommentMapperDto.toCommentResponseDto(comment);
            }
            throw new UserParameterException("Вещь не найдена у Юзера");
        } else {
            throw new UserParameterException("Вещь не найдена у Юзера");
        }
    }

    @Override
    public List<ItemDto> findAllItemByRequest(Long requestId) {
        return toListItemDto(itemRepository.findAllByRequestId(requestId));
    }
}

