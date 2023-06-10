package com.example.mabaya.servises.impls;

import com.example.mabaya.repositories.ProductRepo;
import com.example.mabaya.dto.projections.TopProductProjection;
import com.example.mabaya.entities.Product;
import com.example.mabaya.servises.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepo productRepo;

    @Override
    public Product createdUpdateProduct(Product product) {
        validateSerialNumber(product);
        return productRepo.save(product);
    }

    //TODO: Handle if didn't find any
    @Override
    public TopProductProjection getHighestBiddedProductByCategorty(String category) {
        Optional<TopProductProjection> highestBiddedProducts = productRepo.findTopPromotedProduct(category);
        return highestBiddedProducts.orElse(null);
    }

    @Override
    public Set<Product> getProductFromSerialNumbers(Set<String> productSerialNumbers){
        List<Product> existingProductSerialNumbers = (List<Product>) productRepo.findAllById(productSerialNumbers);
        if (existingProductSerialNumbers.size() != productSerialNumbers.size()) {
            Set<String> inDB = existingProductSerialNumbers.stream().map(Product::getProductSerialNumber).collect(Collectors.toSet());
            Set<String> notInDB = productSerialNumbers.stream().filter(psn -> !inDB.contains(psn)).collect(Collectors.toSet());
            throw new IllegalArgumentException("The serial numbers: "+ notInDB +" are not in the DB");
        }
        return new HashSet<>(existingProductSerialNumbers);
    }

    private void validateSerialNumber(Product product) {
        if(productRepo.existsById(product.getProductSerialNumber())){
            throw new IllegalArgumentException("This serial number is already in the DB");
        }
    }
}
