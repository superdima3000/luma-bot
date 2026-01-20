package com.example.myapp.IT;

import com.example.myapp.model.Item;

import com.example.myapp.model.Size;
import com.example.myapp.repository.ItemRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@SpringBootTest

public class ItemRepositoryIT {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void findAll() {
        List<Item> items = itemRepository.findAll();
        assertNotNull(items);
        assertThat(items.size()).isGreaterThan(0);
    }

    @Test
    @Transactional
    public void findById() {
        Optional<Item> item = itemRepository.findById(1L);
        assertNotNull(item);
        assertThat(item.isPresent()).isTrue();
        assertThat(item.get().getImages().size()).isGreaterThan(1);
        List<Size> itemSizes = item.get().getSizes();
        assertThat(itemSizes.size()).isGreaterThan(0);
    }
}
