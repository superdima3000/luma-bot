package com.example.myapp.IT;

import com.example.myapp.exception.ConflictException;
import com.example.myapp.exception.NotFoundException;
import com.example.myapp.model.create.BrandCreateDto;
import com.example.myapp.service.BrandService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BrandServiceIT {

    @Autowired
    private BrandService brandService;

    @Test
    public void findAll() {
        var result = brandService.getAllBrands();

        System.out.println(result);
        assertNotNull(result);
    }

    @Test
    public void findById() {
        var result = brandService.getBrandById(1L);

        assertNotNull(result);

        assertThrows(NotFoundException.class,
                () -> brandService.getBrandById(1337L));
    }

    @Test
    public void save() {
        var brand = BrandCreateDto.builder()
                .name("Demix")
                .build();

        var result = brandService.createBrand(brand);
        assertNotNull(result);

        var conflict = BrandCreateDto.builder()
                .name("Carhartt")
                .build();

        assertThrows(ConflictException.class,
                () -> brandService.createBrand(conflict));
    }

    @Test
    public void update() {
        var brand = BrandCreateDto.builder()
                .name("Coronavirus")
                .build();


        var result = brandService.updateBrand(1L, brand);
        assertNotNull(result);

        assertThrows(NotFoundException.class,
                () -> brandService.updateBrand(228L, brand));

    }

    @Test
    public void delete() {
        assertDoesNotThrow(() -> brandService.deleteBrand(1L));
        assertThrows(NotFoundException.class, () -> brandService.getBrandById(1L));
    }
}
