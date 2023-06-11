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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductDTO productDTO){
        Product productFromDB =  productService.upsert(productDTO);
        return new ResponseEntity<>(productFromDB, productFromDB.getProductSerialNumber().equals(productDTO.getProductSerialNumber())?
                HttpStatus.CREATED:
                HttpStatus.OK);
    }

    @GetMapping("/serveAd/{category}")
    public ResponseEntity<TopProductProjection> serveAd(@PathVariable @NonNull String category){
        return new ResponseEntity<>(productService.getHighestBiddedProductByCategorty(category),HttpStatus.OK);
    }

    /* TODO: need to change this list to be dto so won't have issues with the parameters from the API
        Parameters from the API could lead to issues, so should be some DTO format to get list of PSN
        and also change it from get to post.
    */
    @GetMapping("/getbypsns")
    public ResponseEntity<Set<Product>> getBySerialNumbers(@RequestParam List<String> serialNumbers){
        return new ResponseEntity<>(productService.getProductsBySerialNumbers(new HashSet<>(serialNumbers)),
                HttpStatus.OK);
    }

    @GetMapping("/{serialNumber}")
    public ResponseEntity<Product> getBySerialNumber(@PathVariable String serialNumber){
        Optional<Product> productFromDB = productService.getBySerialNumber(serialNumber);
        return productFromDB.map(category -> new ResponseEntity<>(category, HttpStatus.FOUND))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{serialNumber}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBySerialNumber(@PathVariable String serialNumber){
        productService.deleteBySerialNumber(serialNumber);
    }



}
