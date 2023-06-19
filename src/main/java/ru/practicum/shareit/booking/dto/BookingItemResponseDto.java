package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingItemResponseDto {
    private Long id;
    private Long bookerId;

    @JsonProperty("start")
    private LocalDateTime startDateTime;

    @JsonProperty("end")
    private LocalDateTime endDateTime;
}
