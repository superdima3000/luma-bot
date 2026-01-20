package com.example.myapp.exception;

import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;

public class ConflictException extends ApiException {
    public ConflictException(String message) {
      super(HttpStatus.CONFLICT, "CONFLICT", message);
    }
}
