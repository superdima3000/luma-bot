package com.example.myapp.service;

import com.example.myapp.model.create.SizeCreateDto;
import com.example.myapp.model.dto.SizeDto;

import java.util.List;

public interface SizeService {
    List<SizeDto> getAllSizes();
    SizeDto getSizeById(Long id);
    List<SizeDto> getSizesByName(String name);
    SizeDto createSize(SizeCreateDto sizeDto);
    SizeDto updateSize(Long id, SizeCreateDto sizeDto);
    void deleteSize(Long id);
    void deleteAllByItemId(Long itemId);
}
