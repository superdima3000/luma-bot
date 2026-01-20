package org.example.http.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.example.http.mapper.JsonMapper;
import org.example.http.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Slf4j
@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private JsonMapper jsonMapper;

    @Override
    public String sendGetRequest(String url) {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    @Override
    public <T> String sendPostRequest(String url, String body) {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String sendPutRequest(String url, String body) {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendDeleteRequest(String url) {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public <T> List<T> getList(String url, Class<T> type) throws JsonProcessingException {
        String json = sendGetRequest(url);
        return jsonMapper.toList(json, type);
    }

    @Override
    public <T, R> R postObject(String url, T dto, Class<R> type){
        try {
            var body = jsonMapper.toJson(dto);
            var responseJson = sendPostRequest(url, body);
            return jsonMapper.toObject(responseJson, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T, R> R putObject(String url, T dto, Class<R> type) {
        try {
            var body = jsonMapper.toJson(dto);
            var responseJson = sendPutRequest(url, body);
            return jsonMapper.toObject(responseJson, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T getObject(String url, Class<T> type) throws JsonProcessingException {
        String json = sendGetRequest(url);
        return jsonMapper.toObject(json, type);
    }
}
