package com.example.myapp.controller;

import com.example.myapp.model.create.SizeCreateDto;
import com.example.myapp.model.dto.SizeDto;
import com.example.myapp.service.SizeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
@Slf4j
public class SizeController {

    private final SizeService sizeService;

    @GetMapping
    public ResponseEntity<List<SizeDto>> getSizes(@RequestParam(required = false) String name) {
        if (name == null) {
            return ResponseEntity.ok().body(sizeService.getAllSizes());
        }

        return ResponseEntity.ok().body(sizeService.getSizesByName(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SizeDto> getSize(@PathVariable Long id) {
        return ResponseEntity.ok().body(sizeService.getSizeById(id));
    }

    @PostMapping
    public ResponseEntity<SizeDto> createSize(@RequestBody @Valid SizeCreateDto size) {
        var created = sizeService.createSize(size);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSize(@PathVariable Long id) {
        sizeService.deleteSize(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/item/{id}")
    public ResponseEntity<Void> deleteSizeByItemId(@PathVariable Long id) {
        log.debug("Deleting sizes");
        sizeService.deleteAllByItemId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SizeDto> updateSize(@PathVariable Long id,
                                               @RequestBody @Valid SizeCreateDto size) {
        return ResponseEntity.ok().body(sizeService.updateSize(id, size));
    }
}
