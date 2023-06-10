package com.example.mabaya.controllers;

import com.example.mabaya.dto.projections.TopProductProjection;
import com.example.mabaya.entities.Product;
import com.example.mabaya.servises.interfaces.ProductService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody @Valid Product newProduct){
        return productService.createdUpdateProduct(newProduct);
    }

    @GetMapping("/serveAd/{category}")
    @ResponseStatus(HttpStatus.OK)
    public TopProductProjection serveAd(@PathVariable @NonNull String category){
        return productService.getHighestBiddedProductByCategorty(category);
    }

    // TODO: need to change this list to be dto so won't have issues with the parameters
    @GetMapping("/countActive")
    @ResponseStatus(HttpStatus.OK)
    public Set<Product> getFildValueBySerialNumber(@RequestParam List<String> serialNumbers){
        return productService.getProductFromSerialNumbers(new HashSet<>(serialNumbers));
    }


}
