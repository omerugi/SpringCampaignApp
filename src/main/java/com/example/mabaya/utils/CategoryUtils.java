package com.example.mabaya.utils;

import com.example.mabaya.dto.CategoryDTO;
import com.example.mabaya.entities.Category;

public class CategoryUtils {
    private CategoryUtils(){}

    public static Category getCategoryFromCategoryDTO(CategoryDTO categoryDTO){
        Category newCategory = new Category();
        newCategory.setId(categoryDTO.getId());
        newCategory.setName(categoryDTO.getName());
        return newCategory;
    }
}
