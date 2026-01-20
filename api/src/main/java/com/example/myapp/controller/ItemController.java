package com.example.myapp.controller;

import com.example.myapp.model.create.ImageCreateDto;
import com.example.myapp.model.create.ItemCreateDto;
import com.example.myapp.model.criteria.ItemSearchCriteria;
import com.example.myapp.model.dto.ImageDto;
import com.example.myapp.model.dto.ItemDto;
import com.example.myapp.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems(@RequestParam(required = false)String name,
                                                  @RequestParam(required = false)Double minPrice,
                                                  @RequestParam(required = false, name = "quantity") Integer minQuantity,
                                                  @RequestParam(required = false)Double maxPrice,
                                                  @RequestParam(required = false, name = "category")List<Long> categories,
                                                  @RequestParam(required = false, name = "brand")List<Long> brands,
                                                  @RequestParam(required = false, name = "sizes")List<String> sizes,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size,
                                                  @RequestParam(defaultValue = "name") String sortBy,
                                                  @RequestParam(defaultValue = "asc") String direction) {
        var criteria = ItemSearchCriteria.builder()
                .name(name)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minQuantity(minQuantity)
                .categories(categories)
                .brands(brands)
                .sizes(sizes)
                .build();

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok().body(itemService.getItemsByCriteria(criteria, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long id) {
        return ResponseEntity.ok().body(itemService.getItemById(id));
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody @Valid ItemCreateDto item) {
        var created = itemService.createItem(item);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long id,
                                               @RequestBody @Valid ItemCreateDto item) {
        log.debug("Request to update Item: {}", item);
        return ResponseEntity.ok().body(itemService.updateItem(id, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{itemId}/images")
    public ResponseEntity<ItemDto> addImageToItem(@PathVariable Long itemId,
                                                  @RequestBody @Valid ImageCreateDto imageCreateDto) {
        ItemDto updatedItem = itemService.addImageToItem(itemId, imageCreateDto);
        return ResponseEntity.ok().body(updatedItem);
    }

    @DeleteMapping("/{itemId}/images/{imageId}")
    public ResponseEntity<ItemDto> removeImageFromItem(@PathVariable Long itemId,
                                                       @PathVariable Long imageId) {
        ItemDto updatedItem = itemService.removeImageFromItem(itemId, imageId);
        return ResponseEntity.ok().body(updatedItem);
    }

    @GetMapping("/{itemId}/images")
    public ResponseEntity<List<ImageDto>> getItemImages(@PathVariable Long itemId) {
        return ResponseEntity.ok().body(itemService.getImagesByItemId(itemId));
    }

}
