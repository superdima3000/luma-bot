package org.example.http.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JsonMapper{

    private final ObjectMapper objectMapper;

    public <T> List<T> toList(final String json, Class<T> type) throws JsonProcessingException {
        JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(List.class, type);
        return objectMapper.readValue(json, javaType);
    }

    public <T> T toObject(final String json, Class<T> type) throws JsonProcessingException {
        return objectMapper.readValue(json, type);
    }

    public <T> String toJson(final T dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }

}
