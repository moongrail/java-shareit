package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestPostTest {
    @Autowired
    private JacksonTester<ItemRequestPost> json;

    @Test
    @SneakyThrows
    void testSerialize() {
        ItemRequestPost itemRequestPost = ItemRequestPost.builder()
                .description("description")
                .build();

        JsonContent<ItemRequestPost> write = json.write(itemRequestPost);

        assertThat(write).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

    @Test
    @SneakyThrows
    void testDeserialize() {
        String jsonContent = "{\"description\":\"description\"}";
        ItemRequestPost object = json.parse(jsonContent).getObject();

        assertThat("description").isEqualTo(object.getDescription());
    }
}