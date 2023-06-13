package com.example.mabaya.servises.impls;

import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.CategoryDTO;
import com.example.mabaya.entities.Category;
import com.example.mabaya.entities.Product;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.repositories.CategoryRepo;
import com.example.mabaya.servises.interfaces.CategoryService;
import com.example.mabaya.servises.interfaces.ProductService;
import com.example.mabaya.utils.CategoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        Category categoryFromDB = opCategoryFromDB.orElseThrow(() -> new AppValidationException(ValidationMsg.notFoundInDb(id)));
        if(!categoryFromDB.getProducts().isEmpty()){
            List<String> productIdSet = categoryFromDB.getProducts().stream().map(Product::getProductSerialNumber).toList();
            throw new AppValidationException(ValidationMsg.cannotDeleteAttachedEntity(id, productIdSet));
        }
        categoryRepo.deleteById(id);
    }
}
