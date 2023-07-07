package ru.practicum.shareit.comments.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class CommentRequestDtoTest {
    @Autowired
    private JacksonTester<CommentRequestDto> json;

    @Test
    @SneakyThrows
    void testSerialize() {

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("text")
                .build();

        JsonContent<CommentRequestDto> write = json.write(commentRequestDto);

        assertThat(write).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }

    @Test
    @SneakyThrows
    void testDeserialize() {
        String jsonContent = "{\"text\":\"text\"}";
        CommentRequestDto object = json.parse(jsonContent).getObject();

        assertThat("text").isEqualTo(object.getText());
    }
}