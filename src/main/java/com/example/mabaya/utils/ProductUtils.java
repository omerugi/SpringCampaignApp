package com.example.mabaya.utils;

import com.example.mabaya.dto.ProductDTO;
import com.example.mabaya.entities.Category;
import com.example.mabaya.entities.Product;

public class ProductUtils {
    private ProductUtils(){}

    public static Product getProductFromProductDTO(ProductDTO productDTO, Category categoryFromDTO){
        Product newProduct = new Product();
        newProduct.setProductSerialNumber(productDTO.getProductSerialNumber());
        newProduct.setTitle(productDTO.getTitle());
        newProduct.setPrice(productDTO.getPrice());
        newProduct.setActive(productDTO.isActive());
        newProduct.setCategory(categoryFromDTO);
        return newProduct;
    }
}
