package com.example.mabaya.controllers;

import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.CampaignDTO;
import com.example.mabaya.entities.Campaign;
import com.example.mabaya.entities.Category;
import com.example.mabaya.entities.Product;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.schedulers.CampaignScheduler;
import com.example.mabaya.servises.interfaces.CampaignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CampaignController.class)
class CampaignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CampaignService campaignService;

    @MockBean
    private CampaignScheduler campaignScheduler;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(){
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }

    @Test
    void TestCreateNewCampaign()throws Exception{
        Category category = new Category();
        category.setId(11L);
        category.setName("cat");

        Product product1 = new Product();
        product1.setProductSerialNumber("1");
        product1.setTitle("1");
        product1.setCategory(category);

        LocalDate date = LocalDate.now();
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setBid(500);
        campaignDTO.setName("test-camp");
        campaignDTO.setStartDate(date);
        campaignDTO.addProductSerialNumber(product1.getProductSerialNumber());

        Campaign campaign = new Campaign();
        campaign.setId(2L);
        campaign.setName("test-camp");
        campaign.setBid(500);
        campaign.setStartDate(date);
        campaign.addProduct(product1);

        when(campaignService.upsert(any(CampaignDTO.class))).thenReturn(campaign);

        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("test-camp")))
                .andExpect(jsonPath("$.bid", is(500.0)))
                .andExpect(jsonPath("$.startDate", is(date.toString())))
                .andExpect(jsonPath("$.products[0].title", is("1")))
                .andExpect(jsonPath("$.products[0].productSerialNumber", is("1")))
                .andExpect(jsonPath("$.products[0].category.id", is(11)))
                .andExpect(jsonPath("$.products[0].category.name", is("cat")))
                ;
    }

    @Test
    void TestUpdateCampaign()throws Exception{
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setId(1L);
        campaignDTO.setBid(500);
        campaignDTO.setName("test-camp");
        campaignDTO.setStartDate(LocalDate.now());
        campaignDTO.addProductSerialNumber("111s");

        Campaign campaign = new Campaign();
        campaign.setId(1L);

        when(campaignService.upsert(any(CampaignDTO.class))).thenReturn(campaign);

        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void TestUpsertWithNullName()throws Exception{
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setId(1L);
        campaignDTO.setBid(500);
        campaignDTO.setStartDate(LocalDate.now());
        campaignDTO.addProductSerialNumber("111");

        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.NULL_NAME)));
    }

    @Test
    void TestUpsertWithInvalidName()throws Exception{
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setName("1");
        campaignDTO.setId(1L);
        campaignDTO.setBid(500);
        campaignDTO.setStartDate(LocalDate.now());
        campaignDTO.addProductSerialNumber("111p");

        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25)));

        campaignDTO.setName("111111111111111111111111111111111");
        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25)));

        campaignDTO.setName("");
        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25)));
    }

    @Test
    void TestUpsertWithInvalidBid()throws Exception{
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setName("test");
        campaignDTO.setId(1L);
        campaignDTO.setBid(-1500);
        campaignDTO.setStartDate(LocalDate.now());
        campaignDTO.addProductSerialNumber("111p");

        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.NUM_BID_NEGATIVE)));
    }

    @Test
    void TestUpsertWithInvalidStartDate()throws Exception{
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setName("test");
        campaignDTO.setId(1L);
        campaignDTO.setBid(1500);
        campaignDTO.addProductSerialNumber("111p");

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.NULL_START_DATE)));
    }

    @Test
    void TestUpsertEmptyProductSerialNumbers()throws Exception{
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setId(1L);
        campaignDTO.setBid(500);
        campaignDTO.setName("test-cam");
        campaignDTO.setStartDate(LocalDate.now());

        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.EMPTY_PSN)));
    }

    @Test
    void TestUpsertInvalidProductSerialNumbers()throws Exception{
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setId(1L);
        campaignDTO.setBid(500);
        campaignDTO.setName("test-cam");
        campaignDTO.setStartDate(LocalDate.now());
        campaignDTO.addProductSerialNumber("2-2-333");

        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.INVALID_PSN)));
        campaignDTO.addProductSerialNumber("");
        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is(ValidationMsg.INVALID_PSN)));

    }

    @Test
    void TestUpsertWithNotInDBProduct()throws Exception{
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setId(1L);
        campaignDTO.setBid(500);
        campaignDTO.setName("test-cam");
        campaignDTO.setStartDate(LocalDate.now());
        campaignDTO.addProductSerialNumber("1");

        when(campaignService.upsert(any(CampaignDTO.class))).thenThrow(new AppValidationException(ValidationMsg.NOT_FOUND_PSN+" "+1));
        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ValidationMsg.NOT_FOUND_PSN+" "+1)));
    }

    @Test
    void TestGetById() throws Exception {
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        when(campaignService.getById(1L)).thenReturn(Optional.of(campaign));

        mockMvc.perform(get("/campaign/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void TestGetByIdNotExists() throws Exception {
        when(campaignService.getById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/campaign/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void TestGetByIdInvalidType() throws Exception {
        mockMvc.perform(get("/campaign/ss")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void TestDeleteById() throws Exception {
        doNothing().when(campaignService).deleteById(1L);
        mockMvc.perform(delete("/campaign/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void TestDeleteIdInvalidType() throws Exception {
        mockMvc.perform(delete("/campaign/ss")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void TestDeleteIdNotInDB() throws Exception {
        doThrow(new AppValidationException("5555L "+ValidationMsg.NOT_FOUND_ID)).when(campaignService).deleteById(5555L);
        mockMvc.perform(delete("/campaign/5555")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("5555L "+ValidationMsg.NOT_FOUND_ID)));
    }

}

