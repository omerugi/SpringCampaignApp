package com.example.mabaya.servises.impls;

import com.example.mabaya.dto.CategoryDTO;
import com.example.mabaya.entities.Category;
import com.example.mabaya.repositories.CategoryRepo;
import com.example.mabaya.servises.interfaces.CategoryService;
import com.example.mabaya.servises.interfaces.ProductService;
import com.example.mabaya.utils.CategoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;


    @Override
    public Category upsert(CategoryDTO categoryDTO) {
        Category newCategory = CategoryUtils.getCategoryFromCategoryDTO(categoryDTO);
        return categoryRepo.save(newCategory);
    }

    @Override
    public Optional<Category> getByid(Long id) {
        return categoryRepo.findById(id);
    }

    @Override
    public Optional<Category> getByName(String name) {
        return categoryRepo.findByName(name);
    }


    @Override
    public void deleteById(Long id) {
        Optional<Category> opCategoryFromDB = categoryRepo.findById(id);
        Category categoryFromDB = opCategoryFromDB.orElseThrow(() -> new IllegalArgumentException("ID is not in the db"));
        if(!categoryFromDB.getProducts().isEmpty()){
            throw new RuntimeException("Cannot delete category with product attached to it");
        }
        categoryRepo.deleteById(id);
    }
}
