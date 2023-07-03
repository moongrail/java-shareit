package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDtoUser;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repositories.BookingRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    public static final long TEST_ID = 1L;
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    private Item item;
    private ItemDto itemDto;
    private ItemResponseDto itemResponseDto;
    private User user;
    private Comment comment;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);
        itemDto = ItemDto.builder()
                .name("test")
                .comments(List.of())
                .owner(User.builder().build())
                .requestId(TEST_ID)
                .description("test")
                .available(true)
                .lastBooking(BookingDtoUser.builder().id(TEST_ID).build())
                .nextBooking(BookingDtoUser.builder().id(2L).build())
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(TEST_ID)
                .name("test")
                .comments(List.of())
                .requestId(TEST_ID)
                .description("test")
                .available(true)
                .lastBooking(BookingResponseDto.builder().id(TEST_ID).build())
                .nextBooking(BookingResponseDto.builder().id(2L).build())
                .build();

        item = Item.builder()
                .id(TEST_ID)
                .owner(User.builder().id(TEST_ID).build())
                .name("test")
                .requestId(TEST_ID)
                .description("test")
                .available(true)
                .build();

        user = User.builder().id(1L).build();

        comment = Comment.builder()
                .id(TEST_ID)
                .author(user)
                .item(item)
                .text("test")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void save_whenInvoked_thenItemSaved() {
        when(userRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(User.builder().build()));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.save(TEST_ID, itemDto, TEST_ID);

        assertEquals(itemResponseDto.getId(), result.getId());
        assertEquals(itemResponseDto.getName(), result.getName());
        assertEquals(itemResponseDto.getDescription(), result.getDescription());
        assertEquals(itemResponseDto.getAvailable(), result.getAvailable());
        assertEquals(itemResponseDto.getRequestId(), result.getRequestId());
        verify(userRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void save_whenUserNotExist_thenThrowUserNotFoundException() {
        when(userRepository.findById(eq(TEST_ID))).thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> itemService.save(TEST_ID, itemDto, TEST_ID));

        assertEquals("Пользователь не найден", userNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    void save_whenUserIdNull_thenThrowItemNotHeaderUserId() {
        ItemNotHeaderUserId itemNotHeaderUserId = assertThrows(ItemNotHeaderUserId.class,
                () -> itemService.save(null, itemDto, TEST_ID));

        assertEquals("Заголовок айди юзера не найден", itemNotHeaderUserId.getMessage());
        verify(userRepository, times(0)).findById(TEST_ID);
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    void patch_whenInvoked_thenItemUpdated() {
        Item updatedItem = Item.builder()
                .id(TEST_ID)
                .name("updated")
                .available(false)
                .description("updated")
                .requestId(TEST_ID)
                .build();

        ItemDto updatedItemDto = ItemDto.builder()
                .id(TEST_ID)
                .name("updated")
                .description("updated")
                .available(false)
                .requestId(TEST_ID)
                .build();
        when(itemRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(item));
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(updatedItem);

        ItemDto result = itemService.patch(TEST_ID, TEST_ID, updatedItemDto);

        assertEquals(updatedItemDto.getName(), result.getName());
        assertEquals(updatedItemDto.getDescription(), result.getDescription());
        assertEquals(updatedItemDto.getAvailable(), result.getAvailable());
        assertEquals(updatedItemDto.getRequestId(), result.getRequestId());
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void patch_whenErrorParametersItem_thenThrowsItemParameterException() {
        ItemDto updatedItemDto = ItemDto.builder()
                .id(TEST_ID)
                .name("updated")
                .description("updated")
                .available(false)
                .build();

        when(itemRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(item));
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.empty());

        ItemParameterException itemParameterException = assertThrows(ItemParameterException.class,
                () -> itemService.patch(TEST_ID, TEST_ID, updatedItemDto));

        assertEquals("Ошибка обновления", itemParameterException.getMessage());
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    void patch_whenUserNotOwnerItem_thenThrowsItemNotFoundException() {
        ItemDto updatedItemDto = ItemDto.builder()
                .id(TEST_ID)
                .name("updated")
                .description("updated")
                .available(false)
                .build();

        when(itemRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(item));

        ItemNotFoundException itemNotFoundException = assertThrows(ItemNotFoundException.class,
                () -> itemService.patch(TEST_ID, 2L, updatedItemDto));

        assertEquals("Вещь не найдена у Юзера", itemNotFoundException.getMessage());
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(0)).findByIdFull(TEST_ID);
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    void findByI_whenInvoked_thenReturnItem() {
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.of(item));
        when(commentRepository.getCommentsByItem_idOrderByCreatedDesc(eq(TEST_ID))).thenReturn(List.of());
        when(bookingRepository
                .findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(eq(TEST_ID), any(), any(), any()))
                .thenReturn(List.of());
        when(bookingRepository
                .findTop1BookingByItemIdAndEndIsAfterAndStatusIs(eq(TEST_ID), any(), any(), any()))
                .thenReturn(List.of());

        ItemResponseDto result = itemService.findById(TEST_ID, TEST_ID);

        assertEquals(itemResponseDto.getId(), result.getId());
        assertEquals(itemResponseDto.getName(), result.getName());
        assertEquals(itemResponseDto.getDescription(), result.getDescription());
        assertEquals(itemResponseDto.getAvailable(), result.getAvailable());
        assertEquals(itemResponseDto.getRequestId(), result.getRequestId());
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(commentRepository, times(1)).getCommentsByItem_idOrderByCreatedDesc(eq(TEST_ID));
        verify(bookingRepository, times(1)).findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
                eq(TEST_ID), any(), any(), any());
        verify(bookingRepository, times(1)).findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
                eq(TEST_ID), any(), any(), any());
    }

    @Test
    void findByI_whenItemNOtExist_thenThenThrowsItemNotFoundException() {
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.empty());

        ItemNotFoundException itemNotFoundException = assertThrows(ItemNotFoundException.class, () ->
                itemService.findById(TEST_ID, TEST_ID));

        assertEquals("Вещь не найдена", itemNotFoundException.getMessage());
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(commentRepository, times(0)).getCommentsByItem_idOrderByCreatedDesc(eq(TEST_ID));
        verify(bookingRepository, times(0)).findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
                eq(TEST_ID), any(), any(), any());
        verify(bookingRepository, times(0)).findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
                eq(TEST_ID), any(), any(), any());
    }

    @Test
    void delete_whenInvoked_thenDeleted() {
        when(itemRepository.existsById(TEST_ID)).thenReturn(true);

        itemService.delete(TEST_ID);

        verify(itemRepository, times(1)).existsById(TEST_ID);
        verify(itemRepository, times(1)).deleteById(TEST_ID);
    }

    @Test
    void delete_whenItemNotExist_thenThrowItemNotFoundException() {
        when(itemRepository.existsById(TEST_ID)).thenReturn(false);
        ItemNotFoundException itemNotFoundException = assertThrows(ItemNotFoundException.class,
                () -> itemService.delete(TEST_ID));

        assertEquals("Вещь не найдена", itemNotFoundException.getMessage());
        verify(itemRepository, times(1)).existsById(TEST_ID);
        verify(itemRepository, times(0)).deleteById(TEST_ID);
    }

    @Test
    void findAllItemByUserId_whenInvoked_thenReturnListOneItem() {
        when(userRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(eq(TEST_ID), any()))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(commentRepository.getCommentsByItem_idOrderByCreatedDesc(eq(TEST_ID))).thenReturn(List.of());
        when(bookingRepository.findFirstByItem_idAndEndBeforeOrderByEndDesc(eq(TEST_ID), any()))
                .thenReturn(Booking.builder().id(1L).booker(user).build());
        when(bookingRepository.findFirstByItem_idAndStartAfterOrderByStartAsc(eq(TEST_ID), any()))
                .thenReturn(Booking.builder().id(2L).booker(user).build());

        List<ItemResponseDto> result = itemService.findAllItemByUserId(TEST_ID, 0, 2);

        assertEquals(1, result.size());
        assertEquals(result.get(0).getLastBooking().getId(), 1L);
        assertEquals(result.get(0).getNextBooking().getId(), 2L);
        verify(itemRepository, times(1)).findAllByOwnerIdOrderByIdAsc(eq(TEST_ID), any());
        verify(commentRepository, times(1)).getCommentsByItem_idOrderByCreatedDesc(eq(TEST_ID));
        verify(bookingRepository, times(1))
                .findFirstByItem_idAndEndBeforeOrderByEndDesc(eq(TEST_ID), any());
        verify(bookingRepository, times(1))
                .findFirstByItem_idAndStartAfterOrderByStartAsc(eq(TEST_ID), any());
        verify(userRepository, times(1)).findById(eq(TEST_ID));

    }

    @Test
    void findAllItemByUserId_whenUserNotExist_thenThrowUserNotFoundException() {
        when(userRepository.findById(eq(TEST_ID))).thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> itemService.findAllItemByUserId(TEST_ID, 0, 2));

        assertEquals("Пользователь не найден", userNotFoundException.getMessage());
        verify(itemRepository, times(0)).findAllByOwnerIdOrderByIdAsc(eq(TEST_ID), any());
        verify(commentRepository, times(0)).getCommentsByItem_idOrderByCreatedDesc(eq(TEST_ID));
        verify(bookingRepository, times(0))
                .findFirstByItem_idAndEndBeforeOrderByEndDesc(eq(TEST_ID), any());
        verify(bookingRepository, times(0))
                .findFirstByItem_idAndStartAfterOrderByStartAsc(eq(TEST_ID), any());
        verify(userRepository, times(1)).findById(eq(TEST_ID));
    }

    @Test
    void findByText_whenInvoked_thenReturnListOneItem() {
        when(itemRepository.searchPage(anyString(), any())).thenReturn(new PageImpl<>(List.of(item)));

        List<ItemDto> result = itemService.findByText("test", 0, 2);

        assertEquals(1, result.size());
        verify(itemRepository, times(1)).searchPage(anyString(), any());
    }

    @Test
    void findByText_whenTextIsEmpty_thenReturnListEmpty() {
        List<ItemDto> result = itemService.findByText("", 0, 2);

        assertEquals(0, result.size());
        verify(itemRepository, times(0)).searchPage(eq(""), any());
    }

    @Test
    void findByText_whenTextIsBlank_thenReturnListEmpty() {
        List<ItemDto> result = itemService.findByText("  ", 0, 2);

        assertEquals(0, result.size());
        verify(itemRepository, times(0)).searchPage(eq("  "), any());
    }

    @Test
    void addComment_whenInvoked_thenCommentSaved() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_IdAndBooker_IdOrderByStartDesc(TEST_ID, TEST_ID))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentResponseDto test = itemService.addComment(TEST_ID, TEST_ID,
                CommentRequestDto.builder().text("test").build());

        assertEquals(comment.getText(), test.getText());
        assertEquals(comment.getCreated(), test.getCreated());
        assertEquals(comment.getAuthor().getName(), test.getAuthorName());
        verify(userRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(bookingRepository, times(1))
                .findByItem_IdAndBooker_IdOrderByStartDesc(TEST_ID, TEST_ID);
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void addComment_whenUserNotExist_thenThrowUserNotFoundException() {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> itemService.addComment(TEST_ID, TEST_ID, CommentRequestDto.builder().text("test").build()));

        assertEquals("Пользователь не найден", userNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(0)).findByIdFull(TEST_ID);
        verify(bookingRepository, times(0))
                .findByItem_IdAndBooker_IdOrderByStartDesc(TEST_ID, TEST_ID);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    void addComment_whenItemNotExist_thenThrowItemNotFoundException() {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.empty());

        ItemNotFoundException itemNotFoundException = assertThrows(ItemNotFoundException.class,
                () -> itemService.addComment(TEST_ID, TEST_ID, CommentRequestDto.builder().text("test").build()));

        assertEquals("Вещь не найдена", itemNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(bookingRepository, times(0))
                .findByItem_IdAndBooker_IdOrderByStartDesc(TEST_ID, TEST_ID);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    void addComment_whenItemNotFound_thenThrowUserParameterException() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_IdAndBooker_IdOrderByStartDesc(TEST_ID, TEST_ID))
                .thenReturn(List.of(booking));

        UserParameterException userParameterException = assertThrows(UserParameterException.class,
                () -> itemService.addComment(TEST_ID, TEST_ID, CommentRequestDto.builder().text("test").build()));

        assertEquals("Вещь не найдена у Юзера", userParameterException.getMessage());
        verify(userRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(bookingRepository, times(1))
                .findByItem_IdAndBooker_IdOrderByStartDesc(TEST_ID, TEST_ID);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    void findAllItemByRequest_whenRequestExist_thenListHaveOneItem() {
        when(itemRepository.findAllByRequestId(TEST_ID)).thenReturn(List.of(item));

        List<ItemDto> result = itemService.findAllItemByRequest(TEST_ID);

        assertEquals(1, result.size());
        verify(itemRepository, times(1)).findAllByRequestId(TEST_ID);
    }

    @Test
    void findAllItemByRequest_whenRequestNotExist_thenListEmpty() {
        when(itemRepository.findAllByRequestId(TEST_ID)).thenReturn(List.of());

        List<ItemDto> result = itemService.findAllItemByRequest(TEST_ID);

        assertEquals(0, result.size());
        verify(itemRepository, times(1)).findAllByRequestId(TEST_ID);
    }
}