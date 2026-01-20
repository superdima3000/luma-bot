package com.example.myapp.mapper;

public interface Mapper<E, D, C>{
    D toDto(E f);
    E toEntity(C t);
}
