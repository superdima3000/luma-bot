package com.example.myapp.model.dto;

import java.util.List;

public class PageResponse<T> {
    private List<T> data;
    private Metadata metadata;

    public static class Metadata {
        private int page;
        private int size;
        private long total;
    }
}
