package com.example.myapp.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@Data
public class ErrorResponse{
    private int status;
    private String code;
    private String message;
    private String path;
    private Map<String, String> details;

    public ErrorResponse(int status, String code, String message, String path) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.path = path;
    }
}
