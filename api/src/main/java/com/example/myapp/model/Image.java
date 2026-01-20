package com.example.myapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "item_images", schema = "shop")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JoinColumn(name = "item_id", nullable = true)
    @ToString.Exclude
    @ManyToOne
    private Item item;

    @Column(nullable = false)
    private String image;

    @Column(name = "is_main")
    private boolean isMain = false;
}
