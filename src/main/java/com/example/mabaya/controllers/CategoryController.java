package com.example.mabaya.controllers;

import com.example.mabaya.dto.CategoryDTO;
import com.example.mabaya.entities.Category;
import com.example.mabaya.servises.interfaces.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> upsert(@RequestBody @Valid CategoryDTO categoryDTO){
        Category categoryFromDB = categoryService.upsert(categoryDTO);
        return new ResponseEntity<>(categoryFromDB, categoryFromDB.getId().equals(categoryDTO.getId())?
                HttpStatus.OK:
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable("id") Long id){
        Optional<Category> categoryFromDB = categoryService.getByid(id);
        return categoryFromDB.map(category -> new ResponseEntity<>(category, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable("id") Long id){
        categoryService.deleteById(id);
    }
}
