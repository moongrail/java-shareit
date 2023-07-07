package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.PaginationParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaginationUtilTest {

    @Test
    void getPaginationWithSortDesc_whenPaginationNull_ThenReturnPage0() {
        Pageable paginationWithSortDesc = PaginationUtil.getPaginationWithSortDesc(null, null);

        assertEquals(0, paginationWithSortDesc.getPageNumber());
        assertEquals(Integer.MAX_VALUE, paginationWithSortDesc.getPageSize());
    }

    @Test
    void getPaginationWithSortDesc_whenPaginationFrom0Size1_ThenReturnPage0() {
        Pageable paginationWithSortDesc = PaginationUtil.getPaginationWithSortDesc(0, 1);

        assertEquals(0, paginationWithSortDesc.getPageNumber());
        assertEquals(1, paginationWithSortDesc.getPageSize());
    }

    @Test
    void getPaginationWithSortDesc_whenPaginationNegative_ThenThrowPaginationParameterException() {
        PaginationParameterException exception = assertThrows(PaginationParameterException.class, () -> {
            PaginationUtil.getPaginationWithSortDesc(-1, -1);
        });

        assertEquals("Неверные параметры пагинации.", exception.getMessage());
    }

    @Test
    void getPaginationWithoutSort_whenPaginationFrom0Size1_ThenReturnPage0() {
        Pageable paginationWithoutSort = PaginationUtil.getPaginationWithoutSort(0, 1);

        assertEquals(0, paginationWithoutSort.getPageNumber());
        assertEquals(1, paginationWithoutSort.getPageSize());
    }

    @Test
    void getPaginationWithoutSort_whenPaginationNull_ThenReturnPage0() {
        Pageable paginationWithoutSort = PaginationUtil.getPaginationWithoutSort(null, null);

        assertEquals(0, paginationWithoutSort.getPageNumber());
        assertEquals(Integer.MAX_VALUE, paginationWithoutSort.getPageSize());
    }

    @Test
    void getPaginationWithoutSort_whenPaginationNegative_ThenThrowPaginationParameterException() {
        PaginationParameterException exception = assertThrows(PaginationParameterException.class,
                () -> PaginationUtil.getPaginationWithoutSort(-1, -1));

        assertEquals("Неверные параметры пагинации.", exception.getMessage());
    }
}