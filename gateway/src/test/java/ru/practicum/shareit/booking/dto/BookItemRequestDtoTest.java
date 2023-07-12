package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookItemRequestDtoTest {
    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    @Test
    @SneakyThrows
    void testSerialize() {

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        BookItemRequestDto bookingCreateDto = BookItemRequestDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        String formatStart = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        String formatEnd = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        JsonContent<BookItemRequestDto> result = json.write(bookingCreateDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(formatStart);
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(formatEnd);
    }

    @Test
    @SneakyThrows
    void testDeserialize() {
        String jsonContent = "{\"itemId\":1,\"start\":\"2022-01-01T00:00:00\",\"end\":\"2022-01-02T00:00:00\"}";

        BookItemRequestDto bookingCreateDto = json.parse(jsonContent).getObject();

        assertThat(1L).isEqualTo(bookingCreateDto.getItemId());
        assertThat("2022-01-01T00:00:00").isEqualTo(bookingCreateDto.getStart()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat("2022-01-02T00:00:00").isEqualTo(bookingCreateDto.getEnd()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }
}