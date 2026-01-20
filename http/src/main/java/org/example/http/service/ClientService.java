package org.example.http.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;

import java.util.List;

public interface ClientService {
    String sendGetRequest(String url);
    <T> String sendPostRequest(String url, String body);
    <T, R> R postObject(String url, T dto, Class<R> type);
    <T> List<T> getList(String url, Class<T> type) throws JsonProcessingException;
    <T> T getObject(String url, Class<T> type) throws JsonProcessingException;
    String sendPutRequest(String url, String body);
    <T, R> R putObject(String url, T dto, Class<R> type);
    void sendDeleteRequest(String url);
}
