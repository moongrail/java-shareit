package ru.practicum.shareit.comments.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemIn(List<Item> items, Sort sort);

    List<Comment> getCommentsByItem_idOrderByCreatedDesc(Long itemId);
}
