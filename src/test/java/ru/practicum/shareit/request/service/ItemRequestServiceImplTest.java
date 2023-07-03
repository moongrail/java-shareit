package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPost;
import ru.practicum.shareit.request.dto.ItemRequestsDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.user.repositories.UserRepository;
import ru.practicum.shareit.util.PaginationUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.request.dto.ItemRequestsDtoMapper.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    public static final long ID_FOR_CORRECT_TEST = 1L;
    private ItemRequestService itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemService itemService;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private ItemRequestPost itemRequestPost;

    private Pageable pageable = PaginationUtil.getPaginationWithSortDesc(0,2);

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemService);
        itemRequestDto = ItemRequestDto.builder()
                .items(List.of())
                .id(ID_FOR_CORRECT_TEST)
                .description("test")
                .created(LocalDateTime.now())
                .build();
        itemRequestPost = ItemRequestPost.builder()
                .description("test")
                .build();

        itemRequest =  ItemRequest.builder()
                .id(ID_FOR_CORRECT_TEST)
                .description("test")
                .created(LocalDateTime.now())
                .requestorId(1L)
                .build();

    }

    @Test
    void getItemRequests_whenInvoked_thenListHaveOneRequest() {
        when(userRepository.existsById(eq(ID_FOR_CORRECT_TEST))).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(eq(ID_FOR_CORRECT_TEST)))
                .thenReturn(List.of(itemRequest));
        when(itemService.findAllItemByRequest(eq(ID_FOR_CORRECT_TEST))).thenReturn(List.of());


        List<ItemRequestDto> itemRequests = itemRequestService.getItemRequests(ID_FOR_CORRECT_TEST);

        assertEquals(1, itemRequests.size());
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdOrderByCreatedDesc(ID_FOR_CORRECT_TEST);
    }

    @Test
    void getItemRequests_whenInvokedEmpty_thenListEmpty() {
        when(userRepository.existsById(eq(ID_FOR_CORRECT_TEST))).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(eq(ID_FOR_CORRECT_TEST)))
                .thenReturn(List.of());


        List<ItemRequestDto> itemRequests = itemRequestService.getItemRequests(ID_FOR_CORRECT_TEST);

        assertEquals(0, itemRequests.size());
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdOrderByCreatedDesc(ID_FOR_CORRECT_TEST);
    }

    @Test
    void getItemRequests_whenUserNotExist_thenThrowUserNotFoundException() {
        when(userRepository.existsById(eq(ID_FOR_CORRECT_TEST))).thenReturn(false);


        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getItemRequests(ID_FOR_CORRECT_TEST));

        assertEquals("Пользователь не найден", userNotFoundException.getMessage());
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, never())
                .findAllByRequestorIdOrderByCreatedDesc(ID_FOR_CORRECT_TEST);
    }

    @Test
    void getItemRequest_whenInvoked_thenReturnRequest() {
        when(userRepository.existsById(eq(ID_FOR_CORRECT_TEST))).thenReturn(true);
        when(itemRequestRepository.findItemRequestById(ID_FOR_CORRECT_TEST)).thenReturn(Optional.of(itemRequest));

        ItemRequestDto itemRequestDto = itemRequestService.getItemRequest(ID_FOR_CORRECT_TEST, ID_FOR_CORRECT_TEST);

        assertEquals(itemRequestDto, itemRequestDto);
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, times(1)).findItemRequestById(ID_FOR_CORRECT_TEST);
    }

    @Test
    void getItemRequest_whenItemNotExist_thenThrowItemNotFoundException() {
        when(userRepository.existsById(eq(ID_FOR_CORRECT_TEST))).thenReturn(true);
        when(itemRequestRepository.findItemRequestById(ID_FOR_CORRECT_TEST)).thenReturn(Optional.empty());

        ItemRequestNotFoundException itemRequestNotFoundException = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequest(ID_FOR_CORRECT_TEST, ID_FOR_CORRECT_TEST));

        assertEquals("Запрос не найден", itemRequestNotFoundException.getMessage());
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, times(1)).findItemRequestById(ID_FOR_CORRECT_TEST);
        verify(itemService, times(0)).findAllItemByRequest(ID_FOR_CORRECT_TEST);
    }

    @Test
    void getAllItemRequests_whenInvoked_thenHaveListOneElement() {
        when(userRepository.existsById(ID_FOR_CORRECT_TEST)).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdNot(ID_FOR_CORRECT_TEST, pageable))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));

        List<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequests(ID_FOR_CORRECT_TEST, 0, 2);

        assertEquals(1, itemRequests.size());
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdNot(ID_FOR_CORRECT_TEST, pageable);
    }

    @Test
    void getAllItemRequests_whenInvoked_thenListEmpty() {
        when(userRepository.existsById(ID_FOR_CORRECT_TEST)).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdNot(ID_FOR_CORRECT_TEST, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        List<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequests(ID_FOR_CORRECT_TEST, 0, 2);

        assertEquals(0, itemRequests.size());
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdNot(ID_FOR_CORRECT_TEST, pageable);
    }

    @Test
    void addItemRequest_whenInvoked_thenRequestSaved() {
        when(userRepository.existsById(ID_FOR_CORRECT_TEST)).thenReturn(true);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto requestSave = itemRequestService.addItemRequest(ID_FOR_CORRECT_TEST, itemRequestPost);

        assertEquals(itemRequestDto.getId(), requestSave.getId());
        assertEquals(itemRequestDto.getCreated(), requestSave.getCreated());
        assertEquals(itemRequestDto.getDescription(), requestSave.getDescription());
        verify(userRepository, times(1)).existsById(ID_FOR_CORRECT_TEST);
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }
}