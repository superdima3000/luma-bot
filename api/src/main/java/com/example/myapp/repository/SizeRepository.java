package com.example.myapp.repository;

import com.example.myapp.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
    List<Size> findByName(String name);
    void deleteAllByItemId(Long itemId);
}
