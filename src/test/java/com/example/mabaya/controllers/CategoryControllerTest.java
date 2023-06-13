package com.example.mabaya.controllers;

import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.CampaignDTO;
import com.example.mabaya.dto.CategoryDTO;
import com.example.mabaya.entities.Category;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.servises.interfaces.CategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void TestCreateCategory() throws Exception {
        Long id = 1L;
        String name = "test";
        CategoryDTO categoryDTO = new CategoryDTO(name);

        Category categoryFromDB = new Category();
        categoryFromDB.setName(name);
        categoryFromDB.setId(id);

        when(categoryService.upsert(any(CategoryDTO.class))).thenReturn(categoryFromDB);


        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.name", is(name)));
    }

    @Test
    void TestUpdateCategory() throws Exception {
        Long id = 1L;
        String name = "test";
        CategoryDTO categoryDTO = new CategoryDTO(id, name);

        Category categoryFromDB = new Category();
        categoryFromDB.setName(name);
        categoryFromDB.setId(id);

        when(categoryService.upsert(any(CategoryDTO.class))).thenReturn(categoryFromDB);


        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.name", is(name)));
    }

    @Test
    void TestUpsertWithShortName() throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO("1");

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25)));
    }

    @Test
    void TestUpsertWithLongName() throws Exception{
        CategoryDTO categoryDTOEmpty = new CategoryDTO(1L,"1111111111111111111111111111");

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTOEmpty)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25)));
    }

    @Test
    void TestUpsertWithEmptyName() throws Exception {
        CategoryDTO categoryDTOEmpty = new CategoryDTO(1L,"");

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTOEmpty)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25)));
    }

    @Test
    void TestUpsertWithNullName() throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO();

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.NULL_NAME)));
    }

    @Test
    void TestGetById() throws Exception{
        Long id = 1L;
        String name = "test";
        Category category = new Category();
        category.setName(name);
        category.setId(id);

        when(categoryService.getByid(1L)).thenReturn(Optional.of(category));
        mockMvc.perform(get("/category/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void TestGetByIdNotExists() throws Exception{
        mockMvc.perform(get("/category/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void TestGetByIdInvalidType() throws Exception{
        mockMvc.perform(get("/category/ss")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void TestDeleteById() throws Exception {
        doNothing().when(categoryService).deleteById(1L);
        mockMvc.perform(delete("/category/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void TestDeleteIdInvalidType() throws Exception {
        mockMvc.perform(delete("/category/ss")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void TestDeleteIdNotInDB() throws Exception {
        doThrow(new AppValidationException("5555L "+ValidationMsg.NOT_FOUND_ID)).when(categoryService).deleteById(5555L);
        mockMvc.perform(delete("/category/5555")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("5555L "+ValidationMsg.NOT_FOUND_ID)));
    }

    @Test
    void TestDeleteAttachedProducts() throws Exception {
        doThrow(new AppValidationException(ValidationMsg.CANNOT_DELETE_CATEGORY_ATTACHED_PRODUCTS)).when(categoryService).deleteById(5555L);
        mockMvc.perform(delete("/category/5555")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ValidationMsg.CANNOT_DELETE_CATEGORY_ATTACHED_PRODUCTS)));
    }

}
