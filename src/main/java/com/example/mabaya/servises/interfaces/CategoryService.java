package com.example.mabaya.servises.interfaces;

import com.example.mabaya.dto.CategoryDTO;
import com.example.mabaya.entities.Category;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CategoryService {

    Category upsert(CategoryDTO categoryDTO);
    Optional<Category> getByid(Long id);

    Optional<Category> getByName(String name);
    void deleteById(Long id);
}
