package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDtoUser;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.comments.dto.CommentRequestDto;
import ru.practicum.shareit.comments.dto.CommentResponseDto;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ItemParameterException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemController.class})
@AutoConfigureMockMvc
class ItemControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @Autowired
    ObjectMapper objectMapper;
    private ItemDto itemDto;
    private List<ItemDto> itemDtoList;
    private ItemResponseDto itemResponseDto;
    private List<ItemResponseDto> itemResponseListDto;
    private CommentResponseDto commentResponseDto;
    private CommentRequestDto commentRequestDto;

    @BeforeEach
    void setUp() {
        itemDto = createItemDto();
        itemResponseDto = createResponseItemDto();
        commentResponseDto = createCommentResponseDto();
        commentRequestDto = createCommentRequestDto();
        itemResponseListDto = itemResponseDtoList();
        itemDtoList = itemDtoList();
    }

    @Test
    @SneakyThrows
    void getAllItems_whenEmptyList_thenStatusOk() {
        when(itemService.findAllItemByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getAllItems_whenListHaveOneItem_thenStatusOk() {
        when(itemService.findAllItemByUserId(anyLong(), anyInt(), anyInt())).thenReturn(itemResponseListDto);


        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemResponseListDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getAllItems_whenUserNotExist_thenStatusNotFound() {
        when(itemService.findAllItemByUserId(anyLong(), anyInt(), anyInt())).thenThrow(UserNotFoundException.class);


        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, 0L)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getItemById_whenInvokedCorrect_thenStatusOk() {
        when(itemService.findById(anyLong(), anyLong())).thenReturn(itemResponseDto);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemResponseDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getItemById_whenInvokedWithNotExistUser_thenStatusNotFound() {
        when(itemService.findById(anyLong(), anyLong())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header(HEADER_USER_ID, 0L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getItemById_whenInvokedWithNotExistItem_thenStatusNotFound() {
        when(itemService.findById(anyLong(), anyLong())).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", 0L)
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addItem_whenInvokedCorrect_thenStatusCreated() {
        when(itemService.save(anyLong(), any(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, 1L)
                        .param("requestId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addItem_whenInvokedNotExistUser_thenStatusNotFound() {
        when(itemService.save(eq(0L), any(), anyLong())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, 0L)
                        .param("requestId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addItem_whenInvokedWithoutName_thenStatusBadRequest() {
        when(itemService.save(anyLong(), any(), anyLong())).thenThrow(ItemParameterException.class);

        ItemDto build = ItemDto.builder()
                .requestId(1L)
                .description("description")
                .available(true)
                .owner(User.builder().id(1L).build())
                .nextBooking(BookingDtoUser.builder().id(1L).build())
                .lastBooking(BookingDtoUser.builder().id(2L).build())
                .comments(List.of())
                .build();

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, 1L)
                        .param("requestId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(build)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addItem_whenInvokedWithoutDescription_thenStatusBadRequest() {
        when(itemService.save(anyLong(), any(), anyLong())).thenThrow(ItemParameterException.class);

        ItemDto build = ItemDto.builder()
                .requestId(1L)
                .name("name")
                .available(true)
                .owner(User.builder().id(1L).build())
                .nextBooking(BookingDtoUser.builder().id(1L).build())
                .lastBooking(BookingDtoUser.builder().id(2L).build())
                .comments(List.of())
                .build();

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, 1L)
                        .param("requestId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(build)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addItem_whenInvokedWithoutAvailable_thenStatusBadRequest() {
        when(itemService.save(anyLong(), any(), anyLong())).thenThrow(ItemParameterException.class);

        ItemDto build = ItemDto.builder()
                .requestId(1L)
                .name("name")
                .description("description")
                .owner(User.builder().id(1L).build())
                .nextBooking(BookingDtoUser.builder().id(1L).build())
                .lastBooking(BookingDtoUser.builder().id(2L).build())
                .comments(List.of())
                .build();

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, 1L)
                        .param("requestId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(build)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void updateItem_whenInvokedCorrect_thenStatusOk() {
        when(itemService.patch(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void updateItem_whenItemNotExist_thenStatusNotFound() {
        when(itemService.patch(eq(0L), anyLong(), any())).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(patch("/items/{itemId}", 0L)
                        .header(HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void deleteItem_whenExistItem_thenStatusOk() {
        mockMvc.perform(delete("/items/{itemId}", 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void deleteItem_whenNotExistItem_thenStatusNotFound() {
        doThrow(ItemNotFoundException.class).when(itemService).delete(0L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/bookings/{bookingId}", 0L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    void searchItemsByText_whenEmpty_TheStatusOk() {
        when(itemService.findByText(anyString(), anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void searchItemsByText_whenHaveOneItem_TheStatusOk() {
        when(itemService.findByText(anyString(), anyInt(), anyInt())).thenReturn(itemDtoList);

        mockMvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDtoList)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addComment_whenInvokedCorrect_thenStatusCreated() {
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(commentResponseDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addComment_whenUserNotExist_thenStatusNotFound() {
        when(itemService.addComment(eq(0L), anyLong(), any())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(HEADER_USER_ID, 0L)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addComment_whenItemNotExist_thenStatusNotFound() {
        when(itemService.addComment(eq(1L), eq(0L), any())).thenThrow(ItemNotFoundException.class);

        mockMvc.perform(post("/items/{itemId}/comment", 0L)
                        .header(HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addComment_whenEmptyComment_thenStatusBadRequest() {
        when(itemService.addComment(eq(1L), eq(1L), any())).thenThrow(ItemParameterException.class);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    private static ItemDto createItemDto() {
        return ItemDto.builder()
                .requestId(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(User.builder().id(1L).build())
                .nextBooking(BookingDtoUser.builder().id(1L).build())
                .lastBooking(BookingDtoUser.builder().id(2L).build())
                .comments(List.of())
                .build();
    }

    private static ItemResponseDto createResponseItemDto() {
        return ItemResponseDto.builder()
                .id(1L)
                .requestId(1L)
                .name("name")
                .description("description")
                .available(true)
                .nextBooking(BookingResponseDto.builder().id(1L).build())
                .lastBooking(BookingResponseDto.builder().id(2L).build())
                .comments(List.of())
                .build();
    }

    private static CommentRequestDto createCommentRequestDto() {
        return CommentRequestDto.builder()
                .text("comment")
                .build();
    }

    private static CommentResponseDto createCommentResponseDto() {
        return CommentResponseDto.builder()
                .id(1L)
                .authorName("name")
                .created(LocalDateTime.now())
                .text("comment")
                .build();
    }

    private static List<ItemResponseDto> itemResponseDtoList() {
        return List.of(createResponseItemDto());
    }

    private static List<ItemDto> itemDtoList() {
        return List.of(createItemDto());
    }
}