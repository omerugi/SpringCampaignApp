package com.example.mabaya.services;

import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.ProductDTO;
import com.example.mabaya.dto.projections.TopProductProjection;
import com.example.mabaya.entities.Category;
import com.example.mabaya.entities.Product;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.repositories.ProductRepo;
import com.example.mabaya.servises.impls.ProductServiceImpl;
import com.example.mabaya.servises.interfaces.CategoryService;
import com.example.mabaya.servises.interfaces.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceImplTest {

    @Mock
    private ProductRepo productRepo;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void testUpsert() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setCategoryName("Electronics");

        Category category = new Category();
        category.setName("Electronics");

        Product product = new Product();
        product.setCategory(category);

        when(categoryService.getByName(anyString())).thenReturn(Optional.of(category));
        when(productRepo.save(any(Product.class))).thenReturn(product);

        Product result = productService.upsert(productDTO);
        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    void testGetHighestBiddedProductByCategory() {
        TopProductProjection projection = mock(TopProductProjection.class);
        when(productRepo.findTopPromotedProduct(anyString())).thenReturn(Optional.of(projection));

        TopProductProjection result = productService.getHighestBiddedProductByCategorty("Electronics");
        assertNotNull(result);
        assertEquals(projection, result);
    }

    @Test
    void testGetProductsByValidSerialNumbers() {
        Set<String> serialNumbers = new HashSet<>(Arrays.asList("SN1", "SN2"));

        Product product1 = new Product();
        product1.setProductSerialNumber("SN1");

        Product product2 = new Product();
        product2.setProductSerialNumber("SN2");

        when(productRepo.findAllById(any())).thenReturn(Arrays.asList(product1, product2));

        Set<Product> result = productService.getProductsBySerialNumbers(serialNumbers);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetProductsByFakeSerialNumbers() {
        Set<String> serialNumbers = new HashSet<>(Arrays.asList("SN1", "SN2"));

        Product product1 = new Product();
        product1.setProductSerialNumber("SN1");

        Product product2 = new Product();
        product2.setProductSerialNumber("SN2");

        when(productRepo.findAllById(any())).thenReturn(List.of(product1));

        Exception exception = assertThrows(AppValidationException.class, () ->
                productService.getProductsBySerialNumbers(serialNumbers));
        assertTrue(exception.getMessage().contains(ValidationMsg.notFoundInDb(product2.getProductSerialNumber())));
    }
}
