package com.example.myapp.service.impl;


import com.example.myapp.exception.NotFoundException;
import com.example.myapp.mapper.SizeMapper;
import com.example.myapp.model.Brand;
import com.example.myapp.model.Size;
import com.example.myapp.model.create.SizeCreateDto;
import com.example.myapp.model.dto.SizeDto;
import com.example.myapp.repository.ItemRepository;
import com.example.myapp.repository.SizeRepository;
import com.example.myapp.service.SizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {

    private final SizeRepository sizeRepository;
    private final ItemRepository itemRepository;
    private final SizeMapper sizeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SizeDto> getAllSizes() {
        return sizeRepository.findAll().stream()
                .map(sizeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SizeDto getSizeById(Long id) {
        var size = findSizeByIdOrThrow(id);
        return sizeMapper.toDto(size);
    }

    @Override
    public List<SizeDto> getSizesByName(String name) {
        List<Size> sizes = sizeRepository.findByName(name);
        return sizes.stream().map(sizeMapper::toDto).toList();
    }

    @Override
    public void deleteAllByItemId(Long itemId){
        sizeRepository.deleteAllByItemId(itemId);
    }

    @Override
    public SizeDto createSize(SizeCreateDto sizeDto) {
        Size size = sizeMapper.toEntity(sizeDto);
        addItemToSize(size, sizeDto);
        size = sizeRepository.save(size);
        return sizeMapper.toDto(size);
    }

    @Override
    public SizeDto updateSize(Long id, SizeCreateDto sizeDto) {
        Size existingSize = findSizeByIdOrThrow(id);

        updateSizeFields(existingSize, sizeDto);
        Size newSize = sizeRepository.save(existingSize);
        return sizeMapper.toDto(newSize);
    }

    @Override
    public void deleteSize(Long id) {
        Size existingSize = findSizeByIdOrThrow(id);
        sizeRepository.delete(existingSize);
    }

    private Size findSizeByIdOrThrow(Long id) {
        return sizeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Size with id " + id + " not found"));
    }

    private void addItemToSize(Size size, SizeCreateDto sizeDto) {
        size.setItem(itemRepository.findById(sizeDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found")));
    }

    private void updateSizeFields(Size existingSize, SizeCreateDto sizeDto) {
        existingSize.setName(sizeDto.getName());
        existingSize.setChestCm(sizeDto.getChestCm());
        existingSize.setLengthCm(sizeDto.getLengthCm());
        existingSize.setWaistCm(sizeDto.getWaistCm());
        addItemToSize(existingSize, sizeDto);
    }
}
