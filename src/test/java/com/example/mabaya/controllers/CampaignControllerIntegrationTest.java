package com.example.mabaya.controllers;

import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.CampaignDTO;
import com.example.mabaya.entities.Campaign;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.repositories.CampaignRepo;
import com.example.mabaya.schedulers.CampaignScheduler;
import com.example.mabaya.servises.impls.CampaignServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@SqlGroup({@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:schema.sql", "classpath:data.sql"}),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:drop.sql")})
class CampaignControllerIntegrationTest {

    @Autowired
    private CampaignScheduler campaignScheduler;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CampaignRepo campaignRepo;

    @Autowired
    private CampaignServiceImpl campaignServiceImpl;

    @Autowired
    private CampaignController campaignController;


    @Test
    @Transactional
    void TestCreateNewCampaign() {
        LocalDate date = LocalDate.now();
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setBid(500);
        campaignDTO.setName("test-camp");
        campaignDTO.setStartDate(date);
        campaignDTO.addProductSerialNumber("1");

        ResponseEntity<Campaign> responseCampaignFromDB = campaignController.upsert(campaignDTO);
        assertNotNull(responseCampaignFromDB.getBody());
        assertEquals(HttpStatus.CREATED,responseCampaignFromDB.getStatusCode());
        Optional<Campaign> opCampFromDB = campaignRepo.findById(responseCampaignFromDB.getBody().getId());
        assertTrue(opCampFromDB.isPresent());
        Campaign campaignFromDB = opCampFromDB.get();
        assertEquals(campaignFromDB.getBid(),campaignDTO.getBid());
        assertEquals(campaignFromDB.getName(),campaignDTO.getName());
        assertNotNull(campaignFromDB.getProducts().stream().filter(product -> Objects.equals(product.getProductSerialNumber()
                , "1")).findFirst().orElse(null));
    }

    @Test
    @Transactional
    void TestUpdateCampaign(){
        Optional<Campaign> opCampaignB4 = campaignRepo.findById(11L);
        assertTrue(opCampaignB4.isPresent());
        Campaign campaignB4 = opCampaignB4.get();

        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setId(11L);
        campaignDTO.setBid(200);
        campaignDTO.setName("test-camp");
        campaignDTO.setStartDate(LocalDate.now());
        campaignDTO.addProductSerialNumber("3");

        ResponseEntity<Campaign> responseCampaignFromDB = campaignController.upsert(campaignDTO);
        assertNotNull(responseCampaignFromDB.getBody());
        assertEquals(HttpStatus.OK,responseCampaignFromDB.getStatusCode());

        Optional<Campaign> opCampFromDB = campaignRepo.findById(responseCampaignFromDB.getBody().getId());
        assertTrue(opCampFromDB.isPresent());

        Campaign campaignFromDB = opCampFromDB.get();
        assertEquals(campaignFromDB.getBid(),campaignDTO.getBid());
        assertEquals(campaignFromDB.getName(),campaignDTO.getName());
        assertNotNull(campaignFromDB.getProducts().stream().filter(product -> Objects.equals(product.getProductSerialNumber()
                , "3")).findFirst().orElse(null));

        assertEquals(campaignB4.getId(),campaignFromDB.getId(),campaignDTO.getId());
        assertEquals(campaignB4.getBid(),campaignDTO.getBid());
        assertEquals(campaignB4.getName(),campaignDTO.getName());
        assertNotNull(campaignB4.getProducts().stream().filter(product -> Objects.equals(product.getProductSerialNumber()
                , "3")).findFirst().orElse(null));
    }


    @Test
    void TestUpsertWithNotInDBProduct()throws Exception{
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setId(11L);
        campaignDTO.setBid(500);
        campaignDTO.setName("test-cam");
        campaignDTO.setStartDate(LocalDate.now());
        campaignDTO.addProductSerialNumber("111");

        try{
            campaignController.upsert(campaignDTO);
        }catch (AppValidationException e){
            assertEquals(ValidationMsg.NOT_FOUND_PSN+" 111",e.getMessage());
        }

    }


    @Test
    void TestGetById() {
        ResponseEntity<Campaign> opFindCampFromDB = campaignController.getById(11L);

        assertNotNull(opFindCampFromDB.getBody());
        assertEquals(HttpStatus.OK, opFindCampFromDB.getStatusCode());
        assertEquals(11L,opFindCampFromDB.getBody().getId());
        assertEquals("camp-test-1",opFindCampFromDB.getBody().getName());
        assertEquals(400.0,opFindCampFromDB.getBody().getBid());


    }

    @Test
    void TestGetByIdNotExists() {
        ResponseEntity<Campaign> opNotFindCampFromDB = campaignController.getById(1L);
        assertNull(opNotFindCampFromDB.getBody());
        assertEquals(HttpStatus.NOT_FOUND, opNotFindCampFromDB.getStatusCode());
    }

    @Test
    void TestDeleteById() {
        Optional<Campaign> campaignB4 = campaignRepo.findById(11L);
        assertTrue(campaignB4.isPresent());
        campaignController.deleteById(11L);
        Optional<Campaign> campaign = campaignRepo.findById(11L);
        assertFalse(campaign.isPresent());
    }

    @Test
    void TestDeleteIdNotInDB() {
        try{
            campaignController.deleteById(11L);
        }catch (AppValidationException e){
            assertEquals(ValidationMsg.NOT_FOUND_ID,e.getMessage());
        }
    }

}

