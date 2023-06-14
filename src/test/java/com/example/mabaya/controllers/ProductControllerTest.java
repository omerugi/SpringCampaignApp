package com.example.mabaya.controllers;

import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.ProductDTO;
import com.example.mabaya.dto.projections.TopProductProjection;
import com.example.mabaya.dto.projections.TopProductProjectionImpl;
import com.example.mabaya.entities.Category;
import com.example.mabaya.entities.Product;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.servises.interfaces.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void TestCreateProduct() throws Exception {
        Long id = 1L;
        String name = "cat-test";
        Category category = new Category();
        category.setName(name);
        category.setId(id);

        String psn = "1";
        String title = "test";

        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle(title);
        productDTO.setCategoryName(name);

        Product productFromDB = new Product();
        productFromDB.setProductSerialNumber(psn);
        productFromDB.setTitle(title);
        productFromDB.setCategory(category);

        when(productService.upsert(any(ProductDTO.class))).thenReturn(productFromDB);


        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productSerialNumber", is(psn)))
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.category.id", is(id.intValue())))
                .andExpect(jsonPath("$.category.name", is(name)));
    }

    @Test
    void TestUpdateProduct() throws Exception {
        Long id = 1L;
        String name = "cat-test";
        Category category = new Category();
        category.setName(name);
        category.setId(id);

        String psn = "1";
        String title = "test";

        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle(title);
        productDTO.setCategoryName(name);
        productDTO.setProductSerialNumber(psn);

        Product productFromDB = new Product();
        productFromDB.setProductSerialNumber(psn);
        productFromDB.setTitle(title);
        productFromDB.setCategory(category);

        when(productService.upsert(any(ProductDTO.class))).thenReturn(productFromDB);


        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productSerialNumber", is(psn)))
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.category.id", is(id.intValue())))
                .andExpect(jsonPath("$.category.name", is(name)));
    }

    @Test
    void TestUpsertWithNotInDBCategory() throws Exception {
        String title = "test";

        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle(title);
        productDTO.setCategoryName("cat-name");

        when(productService.upsert(any(ProductDTO.class))).thenThrow(new AppValidationException(ValidationMsg.NOT_FOUND_CATEGORY_NAME));

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ValidationMsg.NOT_FOUND_CATEGORY_NAME)));
    }

    @Test
    void TestUpsertWithEmptyCategory() throws Exception {
        String title = "test";

        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle(title);

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.EMPTY_CATEGORY_NAME)));
    }

    @Test
    void TestUpsertWithInvalidProductSerialNumber() throws Exception {
        String title = "test";

        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle(title);
        productDTO.setCategoryName("cat-name");
        productDTO.setProductSerialNumber("1----");

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.INVALID_PSN)));

        productDTO.setProductSerialNumber("");

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.INVALID_PSN)));
    }

    @Test
    void TestUpsertWithInvalidTitle() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("1");
        productDTO.setCategoryName("1");
        productDTO.setProductSerialNumber("1");

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.SIZE_CONSTRAINT_TITLE_2_25)));

        productDTO.setTitle("11111111111111111111111111111111111111");

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.SIZE_CONSTRAINT_TITLE_2_25)));

        productDTO.setTitle("");

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.SIZE_CONSTRAINT_TITLE_2_25)));
    }

    @Test
    void TestUpsertWithNullTitle() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setCategoryName("1");
        productDTO.setProductSerialNumber("1");

        mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.NULL_TITLE)));
    }

    @Test
    void TestServeAd() throws Exception {
        String psn = "11";
        String title = "title";
        String category = "test-cat";
        Double bid = 1.0;
        Double price = 1.1;
        TopProductProjection tpp = new TopProductProjectionImpl();
        tpp.setProduct_serial_number(psn);
        tpp.setTitle(title);
        tpp.setCategory(category);
        tpp.setBid(bid);
        tpp.setPrice(price);
        when(productService.getHighestBiddedProductByCategorty(any(String.class))).thenReturn(Optional.of(tpp));
        mockMvc.perform(get("/product/serveAd/test-cat")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_serial_number", is(psn)))
                .andExpect(jsonPath("$.bid", is(bid)))
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.category", is(category)))
                .andExpect(jsonPath("$.price", is(price)))
        ;
    }

    @Test
    void TestServeAdNotFound() throws Exception {
        when(productService.getHighestBiddedProductByCategorty(any(String.class))).thenReturn(Optional.empty());
        mockMvc.perform(get("/product/serveAd/test-cat")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    void TestServeAdInvalidCategory() throws Exception {
        when(productService.getHighestBiddedProductByCategorty(any(String.class))).thenThrow(new AppValidationException(ValidationMsg.NOT_FOUND_CATEGORY_NAME));
        mockMvc.perform(get("/product/serveAd/test-cat")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ValidationMsg.NOT_FOUND_CATEGORY_NAME)))
        ;
    }

    @Test
    void TestGetBySerialNumber() throws Exception {
        Long id = 1L;
        String name = "cat-test";
        Category category = new Category();
        category.setName(name);
        category.setId(id);

        String psn = "1ss";
        String title = "test";

        Product productFromDB = new Product();
        productFromDB.setProductSerialNumber(psn);
        productFromDB.setTitle(title);
        productFromDB.setCategory(category);

        when(productService.getBySerialNumber(psn)).thenReturn(Optional.of(productFromDB));

        mockMvc.perform(get("/product/1ss")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productSerialNumber", is(psn)))
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.category.id", is(id.intValue())))
                .andExpect(jsonPath("$.category.name", is(name)));
    }

    @Test
    void TestGetBySerialNumberNotFound() throws Exception {
        when(productService.getBySerialNumber(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/product/1ss")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void TestDeleteByPsn() throws Exception{
        String psn = "1sss";
        doNothing().when(productService).deleteBySerialNumber(psn);
        mockMvc.perform(delete("/product/1ss")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void TestDeleteByPsnInvalidPsn() throws Exception{
        String psn = "1ss";
        doThrow(new AppValidationException(ValidationMsg.NOT_FOUND_PSN)).when(productService).deleteBySerialNumber(psn);
        mockMvc.perform(delete("/product/1ss")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ValidationMsg.NOT_FOUND_PSN)));
    }

}
