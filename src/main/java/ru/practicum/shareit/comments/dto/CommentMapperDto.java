package ru.practicum.shareit.comments.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapperDto {
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private static final ZoneOffset ZONE_OFFSET = OffsetDateTime.now().getOffset();

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        LocalDateTime created = LocalDateTime.ofInstant(comment.getCreated().toInstant(ZONE_OFFSET), ZONE_ID);

        return CommentResponseDto.builder()
                .id(comment.getId())
                .created(created)
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .build();
    }

    public static Comment fromCommentRequestDto(CommentRequestDto commentRequestDto, Item item, User user,
                                                LocalDateTime now) {
        return Comment.builder()
                .text(commentRequestDto.getText())
                .item(item)
                .author(user)
                .created(now)
                .build();
    }

    public static List<CommentResponseDto> toListComment(List<Comment> comments) {
        return comments.stream().map(CommentMapperDto::toCommentResponseDto).collect(Collectors.toList());
    }
}
