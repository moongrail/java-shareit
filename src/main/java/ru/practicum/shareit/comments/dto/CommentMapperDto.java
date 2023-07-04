package ru.practicum.shareit.comments.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comments.model.Comment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapperDto {
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        LocalDateTime created = LocalDateTime.ofInstant(comment.getCreated(), ZONE_ID);
        return CommentResponseDto.builder()
                .id(comment.getId())
                .created(created)
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .build();
    }

    public static List<CommentResponseDto> toListComment(List<Comment> comments) {
        return comments.stream().map(CommentMapperDto::toCommentResponseDto).collect(Collectors.toList());
    }
}
