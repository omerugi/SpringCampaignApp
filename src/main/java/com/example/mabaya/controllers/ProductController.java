package com.example.mabaya.controllers;

import com.example.mabaya.dto.ProductDTO;
import com.example.mabaya.dto.projections.TopProductProjection;
import com.example.mabaya.entities.Product;
import com.example.mabaya.servises.interfaces.ProductService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductDTO productDTO) {
        Product productFromDB = productService.upsert(productDTO);
        return new ResponseEntity<>(productFromDB, productFromDB.getProductSerialNumber().equals(productDTO.getProductSerialNumber()) ?
                HttpStatus.OK :
                HttpStatus.CREATED);
    }

    @GetMapping("/serveAd/{category}")
    public ResponseEntity<TopProductProjection> serveAd(@PathVariable @NonNull String category) {
        Optional<TopProductProjection> topProductProjection = productService.getHighestBiddedProductByCategorty(category);
        return topProductProjection.map(tpp -> new ResponseEntity<>(tpp, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{serialNumber}")
    public ResponseEntity<Product> getBySerialNumber(@PathVariable String serialNumber) {
        Optional<Product> productFromDB = productService.getBySerialNumber(serialNumber);
        return productFromDB.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{serialNumber}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBySerialNumber(@PathVariable String serialNumber) {
        productService.deleteBySerialNumber(serialNumber);
    }

}
