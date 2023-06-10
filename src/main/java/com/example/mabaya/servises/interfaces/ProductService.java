package com.example.mabaya.servises.interfaces;

import com.example.mabaya.dto.projections.TopProductProjection;
import com.example.mabaya.entities.Product;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface ProductService {

    Set<Product> getProductFromSerialNumbers(Set<String> productSerialNumbers);
    Product createdUpdateProduct(Product product);

    TopProductProjection getHighestBiddedProductByCategorty(String category);
}
