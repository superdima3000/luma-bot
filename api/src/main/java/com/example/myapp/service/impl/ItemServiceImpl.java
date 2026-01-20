package com.example.myapp.service.impl;

import com.example.myapp.exception.NotFoundException;
import com.example.myapp.mapper.ImageMapper;
import com.example.myapp.mapper.SizeMapper;
import com.example.myapp.model.Image;
import com.example.myapp.model.Item;
import com.example.myapp.model.create.ImageCreateDto;
import com.example.myapp.model.create.ItemCreateDto;
import com.example.myapp.model.criteria.ItemSearchCriteria;
import com.example.myapp.model.dto.ImageDto;
import com.example.myapp.model.dto.ItemDto;
import com.example.myapp.repository.BrandRepository;
import com.example.myapp.repository.CategoryRepository;
import com.example.myapp.repository.ItemRepository;
import com.example.myapp.service.ItemService;
import com.example.myapp.specification.ItemSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.myapp.mapper.ItemMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    private final ItemMapper itemMapper;
    private final ImageMapper imageMapper;
    private final SizeMapper sizeMapper;


    @Override
    public List<ItemDto> getAllItems() {
        return itemRepository.findAll().stream().map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long id) {
        var item = findItemByIdOrThrow(id);
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getItemsByCriteria(ItemSearchCriteria criteria, Pageable pageable){
        Specification<Item> spec = createSpec(criteria);

        return itemRepository.findAll(spec, pageable)
                .stream().map(itemMapper::toDto).toList();
    }

    @Override
    public List<ItemDto> getItemsByName(String name) {
        var items = itemRepository.findByNameContainingIgnoreCase(name);
        if (items.isEmpty()) {
            throw new NotFoundException("Item not found");
        }

        return items.stream().map(itemMapper::toDto).toList();
    }

    @Override
    public ItemDto createItem(ItemCreateDto itemCreateDto) {
        Item item = itemMapper.toEntity(itemCreateDto);
        setBrandAndCategory(item, itemCreateDto.getBrand(), itemCreateDto.getCategory());
        addImagesToItem(item, itemCreateDto.getImages());
        item = itemRepository.save(item);

        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(Long id, ItemCreateDto itemCreateDto) {
        Item existingItem = findItemByIdOrThrow(id);

        updateFields(existingItem, itemCreateDto);
        addImagesToItem(existingItem, itemCreateDto.getImages());
        setBrandAndCategory(existingItem, itemCreateDto.getBrand(), itemCreateDto.getCategory());

        Item item = itemRepository.save(existingItem);
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto addImageToItem(Long itemId, ImageCreateDto imageCreateDto) {
        Item item = findItemByIdOrThrow(itemId);
        Image image = imageMapper.toEntity(imageCreateDto);
        item.addImage(image);

        Item updatedItem = itemRepository.save(item);
        return itemMapper.toDto(updatedItem);
    }

    @Override
    public ItemDto removeImageFromItem(Long itemId, Long imageId) {
        Item item = findItemByIdOrThrow(itemId);

        boolean removed = item.getImages().removeIf(image -> image.getId().equals(imageId));
        if (!removed) {
            throw new NotFoundException("Image not found with id: " + imageId);
        }

        Item updatedItem = itemRepository.save(item);
        return itemMapper.toDto(updatedItem);
    }

    @Override
    public void deleteItem(Long id) {
        Item item = findItemByIdOrThrow(id);
        itemRepository.delete(item);
    }

    private Item findItemByIdOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + id));
    }

    private void setBrandAndCategory(Item item, Long brandId, Long categoryId) {
        var brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new NotFoundException("Brand not found with id: " + brandId));

        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + categoryId));

        item.setBrand(brand);
        item.setCategory(category);
    }

    private void addImagesToItem(Item item, List<ImageCreateDto> imageDtos) {
        item.clearImages();
        if (imageDtos != null) {
            for (var imageDto : imageDtos) {
                var image = imageMapper.toEntity(imageDto);
                item.addImage(image);
            }
        }

    }

    @Override
    public List<ImageDto> getImagesByItemId(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        return item.getImages().stream()
                .map(imageMapper::toDto)
                .collect(Collectors.toList());
    }

    private void updateFields(Item item, ItemCreateDto itemCreateDto) {
        item.setName(itemCreateDto.getName());
        item.setDescription(itemCreateDto.getDescription());
        item.setPrice(itemCreateDto.getPrice());
        item.setWeight(itemCreateDto.getWeight());
        item.setQuantity(itemCreateDto.getQuantity());
        setBrandAndCategory(item, itemCreateDto.getBrand(), itemCreateDto.getCategory());
    }
    
    private Specification<Item> createSpec(ItemSearchCriteria criteria) {
        List<Specification<Item>> specs = new ArrayList<>();
        specs.add(ItemSpecification.hasName(criteria.getName()));
        specs.add(ItemSpecification.hasPriceBetween(criteria.getMinPrice(), criteria.getMaxPrice()));

        if (criteria.getBrands() != null && !criteria.getBrands().isEmpty()) {
            Specification<Item> brandSpec = criteria.getBrands().stream()
                    .map(ItemSpecification::hasBrand)
                    .reduce(Specification::or)
                    .orElse(((root, query, cb) -> cb.conjunction()));
            specs.add(brandSpec);
        }

        if (criteria.getCategories() != null && !criteria.getCategories().isEmpty()) {
            Specification<Item> categorySpec = criteria.getCategories().stream()
                    .map(ItemSpecification::hasCategory)
                    .reduce(Specification::or)
                    .orElse(((root, query, cb) -> cb.conjunction()));
            specs.add(categorySpec);
        }

        if (criteria.getSizes() != null && !criteria.getSizes().isEmpty()) {
            Specification<Item> sizeSpec = criteria.getSizes().stream()
                    .map(ItemSpecification::hasSize)
                    .reduce(Specification::or)
                    .orElse(((root, query, cb) -> cb.conjunction()));
            specs.add(sizeSpec);
        }

        if (criteria.getMinQuantity() != null){
            Specification<Item> minQuantitySpec = ItemSpecification.hasMinQuantity(criteria.getMinQuantity());
            specs.add(minQuantitySpec);
        }

        specs = specs.stream().filter(Objects::nonNull).toList();

        return specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction());

    }
}
