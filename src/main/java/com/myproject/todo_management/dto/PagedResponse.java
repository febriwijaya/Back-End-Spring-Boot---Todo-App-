package com.myproject.todo_management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> data;
    private int page;
    private int size;
    @JsonProperty("total_elements")
    private Long totalElements;
    @JsonProperty("total_pages")
    private int totalPages;
//    private boolean last;
}
