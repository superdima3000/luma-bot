package com.example.myapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items", schema = "shop")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 256)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double price;

    private Double weight;

    private Integer quantity;



    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Size> sizes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public void addImage(Image image) {
        images.add(image);
        image.setItem(this);
    }

    public void addSize(Size size) {
        sizes.add(size);
        size.setItem(this);
    }

    public void clearImages() {
        /*for (Image image : images) {
            image.setItem(null);
        }*/
        images.clear();
    }
}
