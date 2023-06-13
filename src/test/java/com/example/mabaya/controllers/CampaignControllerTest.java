package com.example.mabaya.controllers;

import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.CampaignDTO;
import com.example.mabaya.entities.Campaign;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.schedulers.CampaignScheduler;
import com.example.mabaya.servises.interfaces.CampaignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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


    @Test
    void TestGetByIdCampaignExists() throws Exception {
        Campaign campaign = new Campaign();
        when(campaignService.getById(1L)).thenReturn(Optional.of(campaign));

        mockMvc.perform(get("/campaign/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void TestGetByIdCampaignNotExists() throws Exception {
        when(campaignService.getById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/campaign/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void TestGetByIdNotValidType() throws Exception {
        mockMvc.perform(get("/campaign/ss")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"ss\"")));
    }

    @Test
    void TestCreateNewCampaign()throws Exception{
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setBid(500);
        campaignDTO.setName("test-cam");
        campaignDTO.setStartDate(LocalDate.now());
        Campaign campaign = new Campaign();
        campaign.setId(2L);
        when(campaignService.upsert(any(CampaignDTO.class))).thenReturn(campaign);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)));;
    }

    @Test
    void TestUpdateNewCampaign()throws Exception{
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setId(1L);
        campaignDTO.setBid(500);
        campaignDTO.setName("test-cam");
        campaignDTO.setStartDate(LocalDate.now());
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        when(campaignService.upsert(any(CampaignDTO.class))).thenReturn(campaign);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void TestUpsertWithInvalidName()throws Exception{
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setId(1L);
        campaignDTO.setBid(500);
        campaignDTO.setStartDate(LocalDate.now());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is("Name cannot be empty")));
    }

    @Test
    void TestUpsertWithInvalidBid()throws Exception{
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setName("test");
        campaignDTO.setId(1L);
        campaignDTO.setBid(-1500);
        campaignDTO.setStartDate(LocalDate.now());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is("Bid cannot be negative")));
    }

    @Test
    void TestUpsertWithInvalidStartDate()throws Exception{
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setName("test");
        campaignDTO.setId(1L);
        campaignDTO.setBid(1500);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].defaultMessage", is("StartDate cannot be null")));
    }

    @Test
    void TestUpdateStartDate() throws Exception {
        doNothing().when(campaignScheduler).deactivateOldCampaigns();
        mockMvc.perform(post("/campaign/runscheduler")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void TestDeleteByIdCampaignExists() throws Exception {
        doNothing().when(campaignService).deleteById(1L);
        mockMvc.perform(delete("/campaign/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void TestDeleteInvalidId() throws Exception {
        doNothing().when(campaignService).deleteById(1L);
        mockMvc.perform(delete("/campaign/ss")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void TestDeleteIdNotInDB() throws Exception {
        doThrow(new AppValidationException(ValidationMsg.notFoundInDb(5555L))).when(campaignService).deleteById(5555L);
        mockMvc.perform(delete("/campaign/5555")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ValidationMsg.notFoundInDb(5555L))));
    }

}

