package com.example.myapp.IT;

import com.example.myapp.exception.NotFoundException;
import com.example.myapp.model.create.ImageCreateDto;
import com.example.myapp.model.create.ItemCreateDto;

import com.example.myapp.model.criteria.ItemSearchCriteria;
import com.example.myapp.repository.ItemRepository;
import com.example.myapp.service.ItemService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
public class ItemServiceIT {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void findAll() {
        var items = itemService.getAllItems();
        assertNotNull(items);
        assertThat(items.size()).isGreaterThan(0);
    }

    @Test
    public void findById() {
        var item = itemService.getItemById(1L);
        assertNotNull(item);
        var sizes = item.getSizes();
        assertThat(sizes.size()).isGreaterThan(0);
    }

    @Test
    public void findByNonExistingId() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(100L));
    }

    @Test
    @Transactional
    public void save() {
        String name = "Demix Shoes Sport";
        var item = ItemCreateDto.builder()
                .name(name)
                .description("Demix Shoes Sport for real nigga runners")
                .brand(1L)
                .category(1L)
                .weight(100.1)
                .price(10.1)
                .images(List.of(new ImageCreateDto("image1", true)))
                .build();

        var result = itemService.createItem(item);

        assertNotNull(result);

        var searchResult = itemService.getItemsByName(name);

        assertNotNull(searchResult);

    }

    /*@Test
    public void findByCriteria(){
        var criteria = ItemSearchCriteria.builder()
                .sizes(Arrays.asList("M", "L"))
                .build();

        var result = itemService.getItemsByCriteria(criteria);
        System.out.println(result);
        assertThat(result.size()).isGreaterThan(0);
    }*/

    @Test
    public void addImage() {
        var image = ImageCreateDto.builder()
                .image("image1")
                .isMain(false)
                .build();

        var item = itemService.getItemById(1L);
        var before = item.getImages().size();
        var result = itemService.addImageToItem(1L, image);
        var after = result.getImages().size();
        assertThat(before < after).isTrue();

    }

    @Test
    public void removeImage() {
        var image = ImageCreateDto.builder()
                .image("image1")
                .isMain(false)
                .build();
        var item = itemService.getItemById(1L);
        var preResult = itemService.addImageToItem(1L, image);
        var before = preResult.getImages().size();
        var result = itemService.removeImageFromItem(preResult.getId(),
                preResult.getImages().getLast().getId());
        var after = result.getImages().size();
        assertThat(before > after).isTrue();
    }
}
