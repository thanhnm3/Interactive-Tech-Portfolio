package com.portfolio.shared.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Paginated response wrapper
 * @param <T> Content item type
 */
@Getter
@Builder
public class PageResponse<T> {

    private final List<T> contentList;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElementCount;
    private final int totalPageCount;
    private final boolean isFirst;
    private final boolean isLast;
    private final boolean hasNext;
    private final boolean hasPrevious;

    /**
     * Create page response from Spring Data Page
     * @param contentList Page content
     * @param pageNumber Current page number
     * @param pageSize Page size
     * @param totalElementCount Total elements
     * @param <T> Content type
     * @return Page response
     */
    public static <T> PageResponse<T> of(List<T> contentList, int pageNumber, int pageSize, long totalElementCount) {
        int totalPageCount = (int) Math.ceil((double) totalElementCount / pageSize);

        return PageResponse.<T>builder()
                .contentList(contentList)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElementCount(totalElementCount)
                .totalPageCount(totalPageCount)
                .isFirst(pageNumber == 0)
                .isLast(pageNumber >= totalPageCount - 1)
                .hasNext(pageNumber < totalPageCount - 1)
                .hasPrevious(pageNumber > 0)
                .build();
    }
}
