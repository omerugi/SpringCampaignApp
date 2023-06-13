package com.example.mabaya.servises.interfaces;

import com.example.mabaya.dto.ProductDTO;
import com.example.mabaya.dto.projections.TopProductProjection;
import com.example.mabaya.entities.Product;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public interface ProductService {
    Product upsert(ProductDTO product);
    Set<Product> getProductsBySerialNumbers(Set<String> productSerialNumbers);
    TopProductProjection getHighestBiddedProductByCategorty(String category);

    Optional<Product> getBySerialNumber(String serialNumbers);
    Optional<Product> getByTitle(String title);

    void deleteBySerialNumber(String serialNumber);
}
