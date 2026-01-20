package com.example.myapp.service;


import com.example.myapp.model.create.ImageCreateDto;
import com.example.myapp.model.create.ItemCreateDto;
import com.example.myapp.model.criteria.ItemSearchCriteria;
import com.example.myapp.model.dto.ImageDto;
import com.example.myapp.model.dto.ItemDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<ItemDto> getAllItems();
    ItemDto getItemById(Long id);
    List<ItemDto> getItemsByName(String name);
    ItemDto createItem(ItemCreateDto itemCreateDto);
    ItemDto updateItem(Long id, ItemCreateDto itemCreateDto);
    ItemDto addImageToItem(Long itemId, ImageCreateDto imageCreateDto);
    ItemDto removeImageFromItem(Long itemId, Long imageId);
    List<ItemDto> getItemsByCriteria(ItemSearchCriteria itemSearchCriteria, Pageable pageable);
    void deleteItem(Long id);
    List<ImageDto> getImagesByItemId(Long itemId);
}
