package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exceptions.ItemParameterException;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPost;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemRequestController.class})
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    ObjectMapper objectMapper;

    private ItemRequestDto itemRequestDto;
    private List<ItemRequestDto> itemRequestListDto;
    private ItemRequestPost itemRequestPost;

    @BeforeEach
    void setUp() {
        itemRequestDto = createItemRequestDto();
        itemRequestListDto = createItemRequestDtoList();
        itemRequestPost = createItemRequestPost();
    }

    @Test
    @SneakyThrows
    void getItemRequests_whenEmpty_thenStatusOk() {
        when(itemRequestService.getItemRequests(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getItemRequests_whenListHaveOneRequest_thenStatusOk() {
        when(itemRequestService.getItemRequests(anyLong())).thenReturn(itemRequestListDto);

        mockMvc.perform(get("/requests")
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestListDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getItemRequest_whenInvoked_thenStatusOk() {
        when(itemRequestService.getItemRequest(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getItemRequest_whenUserNotExist_thenStatusNotFound() {
        when(itemRequestService.getItemRequest(eq(0L), anyLong())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header(HEADER_USER_ID, "0"))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getItemRequest_whenRequestNotExist_thenStatusNotFound() {
        when(itemRequestService.getItemRequest(eq(1L), eq(0L))).thenThrow(ItemRequestNotFoundException.class);

        mockMvc.perform(get("/requests/{requestId}", 0L)
                        .header(HEADER_USER_ID, "1"))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getAllItemRequests_whenInvoked_thenHaveOneRequestStatusOk() {
        when(itemRequestService.getAllItemRequests(anyLong(), anyInt(), anyInt())).thenReturn(itemRequestListDto);

        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestListDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void getAllItemRequests_whenInvokedEmptyList_thenStatusOk() {
        when(itemRequestService.getAllItemRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addItemRequest_whenInvokedCorrect_thenStatusCreated() {
        when(itemRequestService.addItemRequest(anyLong(), any())).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(HEADER_USER_ID, "1")
                        .content(objectMapper.writeValueAsString(itemRequestPost))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addItemRequest_whenEmptyDescription_thenStatusBadRequest() {
        when(itemRequestService.addItemRequest(anyLong(), any())).thenThrow(ItemParameterException.class);

        mockMvc.perform(post("/requests")
                        .header(HEADER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void addItemRequest_whenUserNotExist_thenStatusNotFound() {
        when(itemRequestService.addItemRequest(eq(0L), any())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(post("/requests")
                        .header(HEADER_USER_ID, "0")
                        .content(objectMapper.writeValueAsString(itemRequestPost))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    private static ItemRequestDto createItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("test")
                .items(List.of(ItemDto.builder().id(1L).build()))
                .build();
    }

    private static ItemRequestPost createItemRequestPost() {
        return ItemRequestPost.builder()
                .description("test")
                .build();
    }

    private static List<ItemRequestDto> createItemRequestDtoList() {
        return List.of(createItemRequestDto());
    }
}