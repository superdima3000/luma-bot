package com.example.myapp.specification;

import com.example.myapp.model.Item;
import org.springframework.data.jpa.domain.Specification;

public class ItemSpecification {

    public static Specification<Item> hasName(String name){
        return (root, query, cb) ->
            name == null ? null : cb.like(cb.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%");
    }

    public static Specification<Item> hasBrand(Long brand){
        return (root, query, cb) ->
                brand == null ? null : cb.equal(root.get("brand").get("id"), brand);
    }

    public static Specification<Item> hasPriceBetween(Double lower, Double higher){
        return (root, query, cb) -> {
            if (lower == null && higher == null) {
                return cb.conjunction();
            }
            if (lower != null && higher != null) {
                return cb.between(root.get("price"), lower, higher);
            }
            if (lower == higher){
                return cb.equal(root.get("price"), lower);
            }
            if (lower != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), lower);
            }
            return cb.lessThanOrEqualTo(root.get("price"), higher);
        };
    }

    public static Specification<Item> hasMinQuantity(Integer minQuantity) {
        return ((root, query, cb) ->
                minQuantity == null ? null : cb.greaterThanOrEqualTo(root.get("quantity"), minQuantity));
    }

    public static Specification<Item> hasCategory(Long category){
        return ((root, query, cb) ->
                category == null ? null : cb.equal(root.get("category").get("id"), category));
    }

    public static Specification<Item> hasSize(String size){
        return (root, query, cb) -> {
            if (size == null) {
                return null;
            }
            return cb.equal(root.join("sizes").get("name"), size);
        };
    }
}
