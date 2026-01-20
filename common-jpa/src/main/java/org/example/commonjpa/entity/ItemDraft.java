package org.example.commonjpa.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_drafts")
public class ItemDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Double weight;
    @OneToMany(
            mappedBy = "itemDraft",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ImageDraft> images;
    private Long brand;
    private Long category;
    @JsonProperty("quantity")
    private Integer quantity;
    private Long sessionId;
    private Long itemId;
    @ElementCollection
    @CollectionTable(
            name = "product_draft_sizes",
            joinColumns = @JoinColumn(name = "product_draft_id")
    )
    @Column(name = "size")
    @Builder.Default
    private List<String> sizes = new ArrayList<>();

    public void addSize(String size) {
        sizes.add(size);
    }

    public void removeSize(String size) {
        sizes.remove(size);
    }

    public void addImage(ImageDraft image) {
        images.add(image);
        image.setItemDraft(this);
    }

    public void removeImage(ImageDraft image) {
        images.remove(image);
        image.setItemDraft(null);
    }
}
