package com.example.mabaya.servises.impls;

import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.ProductDTO;
import com.example.mabaya.entities.Category;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.repositories.ProductRepo;
import com.example.mabaya.dto.projections.TopProductProjection;
import com.example.mabaya.entities.Product;
import com.example.mabaya.servises.interfaces.CategoryService;
import com.example.mabaya.servises.interfaces.ProductService;
import com.example.mabaya.utils.ProductUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private CategoryService categoryService;


    @Override
    public Product upsert(ProductDTO productDTO) {
        Product product = createProductFromDTO(productDTO);
        return productRepo.save(product);
    }

    @Override
    public TopProductProjection getHighestBiddedProductByCategorty(String category) {
        Optional<TopProductProjection> highestBiddedProducts = productRepo.findTopPromotedProduct(category);
        return highestBiddedProducts.orElseThrow(null);
    }

    @Override
    public Set<Product> getProductsBySerialNumbers(Set<String> productSerialNumbers){
        List<Product> existingProductSerialNumbers = productRepo.findAllById(productSerialNumbers);
        if (existingProductSerialNumbers.size() != productSerialNumbers.size()) {
            Set<String> inDB = existingProductSerialNumbers.stream().map(Product::getProductSerialNumber).collect(Collectors.toSet());
            List<String> notInDB = productSerialNumbers.stream().filter(psn -> !inDB.contains(psn)).toList();
            throw new AppValidationException(ValidationMsg.notFoundInDb(notInDB));
        }
        return new HashSet<>(existingProductSerialNumbers);
    }

    @Override
    public Optional<Product> getBySerialNumber(String serialNumbers) {
        return productRepo.findById(serialNumbers);
    }

    @Override
    public Optional<Product> getByTitle(String title) {return productRepo.findByTitle(title);}

    @Override
    public void deleteBySerialNumber(String serialNumber) {
        Optional<Product> opProductFromDB = getBySerialNumber(serialNumber);
        Product productFromDB = opProductFromDB.orElseThrow(()-> new AppValidationException(ValidationMsg.notFoundInDb(serialNumber)));
        productRepo.delete(productFromDB);
    }

    private Product createProductFromDTO(ProductDTO productDTO){
        Optional<Category> opCategoryFromDB = categoryService.getByName(productDTO.getCategoryName());
        Category categoryFromDB = opCategoryFromDB.orElseThrow(()-> new AppValidationException(ValidationMsg.notFoundInDb(productDTO.getCategoryName())));
        return ProductUtils.getProductFromProductDTO(productDTO,categoryFromDB);
    }

}
