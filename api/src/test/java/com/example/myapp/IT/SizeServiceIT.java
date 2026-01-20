package com.example.myapp.IT;

import com.example.myapp.exception.NotFoundException;
import com.example.myapp.model.Size;
import com.example.myapp.model.create.SizeCreateDto;
import com.example.myapp.service.SizeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SizeServiceIT {

    @Autowired
    private SizeService sizeService;

    @Test
    public void findAll() {
        var result = sizeService.getAllSizes();
        System.out.println(result);
        assertNotNull(result);
    }

    @Test
    public void findById() {
        var result = sizeService.getSizeById(1L);
        System.out.println(result);
        assertNotNull(result);

        assertThrows(NotFoundException.class,
                () -> sizeService.getSizeById(11111L));
    }

    @Test
    public void findByName(){
        var result = sizeService.getSizesByName("M");
        System.out.println(result);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        var nonExistentResult = sizeService.getSizesByName("bigAssSize");
        assertTrue(nonExistentResult.isEmpty());
    }

    @Test
    public void save(){
        var size = SizeCreateDto.builder()
                .name("XXL")
                .waistCm(100.1)
                .itemId(1L)
                .build();

        var result = sizeService.createSize(size);
        assertNotNull(result);

        var size1 = SizeCreateDto.builder()
                .name("M")
                .waistCm(100.1)
                .itemId(228L)
                .build();

        assertThrows(NotFoundException.class,
                () -> sizeService.createSize(size1));
    }

    @Test
    public void update() {
        var size = SizeCreateDto.builder()
                .name("M")
                .waistCm(100.1)
                .itemId(1L)
                .build();

        var result = sizeService.updateSize(1L, size);
        assertNotNull(result);
    }

    @Test
    public void delete() {
        sizeService.deleteSize(1L);
        assertThrows(NotFoundException.class,
                () -> sizeService.getSizeById(1L));
    }
}
