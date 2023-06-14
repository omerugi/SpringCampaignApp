package com.example.mabaya.controllers;

import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.dto.CampaignDTO;
import com.example.mabaya.entities.Campaign;
import com.example.mabaya.entities.Product;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.repositories.CampaignRepo;
import com.example.mabaya.repositories.ProductRepo;
import com.example.mabaya.schedulers.CampaignScheduler;
import com.example.mabaya.servises.impls.CampaignServiceImpl;

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
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@SpringBootTest
@SqlGroup({@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:schema.sql", "classpath:data.sql"}),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:drop.sql")})
class CampaignControllerIntegrationTest {

    @Autowired
    private CampaignScheduler campaignScheduler;

    @Autowired
    private CampaignRepo campaignRepo;

    @Autowired
    private CampaignServiceImpl campaignServiceImpl;

    @Autowired
    private CampaignController campaignController;

    @Autowired
    private ProductRepo productRepo;

    @Test
    @Transactional
    void TestCreateNewCampaign() {
        LocalDate date = LocalDate.now();
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setBid(500);
        campaignDTO.setName("test-camp");
        campaignDTO.setStartDate(date);
        campaignDTO.addProductSerialNumber("1");

        ResponseEntity<Campaign> responseCampaign = campaignController.upsert(campaignDTO);
        assertNotNull(responseCampaign.getBody());
        assertEquals(HttpStatus.CREATED, responseCampaign.getStatusCode());
        Campaign campaignSaved = responseCampaign.getBody();

        assertEquals(campaignSaved.getBid(),campaignDTO.getBid());
        assertEquals(campaignSaved.getName(),campaignDTO.getName());
        assertEquals(campaignSaved.getStartDate(),campaignDTO.getStartDate());
        assertNotNull(campaignSaved.getProducts().stream().filter(product -> Objects.equals(product.getProductSerialNumber()
                , "1")).findFirst().orElse(null));

        Optional<Campaign> opCampFromDB = campaignRepo.findById(responseCampaign.getBody().getId());
        assertTrue(opCampFromDB.isPresent());
        Campaign campaignFromDB = opCampFromDB.get();
        assertEquals(campaignFromDB.getBid(),campaignDTO.getBid());
        assertEquals(campaignFromDB.getName(),campaignDTO.getName());
        assertNotNull(campaignFromDB.getProducts().stream().filter(product -> Objects.equals(product.getProductSerialNumber()
                , "1")).findFirst().orElse(null));
    }

    @Test
    @Transactional
    void TestCreateNewCampaignWithPassStartDate() {
        LocalDate date = LocalDate.now().minusDays(11);
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setBid(500);
        campaignDTO.setName("test-camp");
        campaignDTO.setStartDate(date);
        campaignDTO.addProductSerialNumber("1");
        campaignDTO.setActive(true);

        ResponseEntity<Campaign> responseCampaign = campaignController.upsert(campaignDTO);
        assertNotNull(responseCampaign.getBody());
        assertEquals(HttpStatus.CREATED, responseCampaign.getStatusCode());
        Campaign campaignSaved = responseCampaign.getBody();

        assertEquals(campaignSaved.getBid(),campaignDTO.getBid());
        assertEquals(campaignSaved.getName(),campaignDTO.getName());
        assertEquals(campaignSaved.getStartDate(),campaignDTO.getStartDate());
        assertNotNull(campaignSaved.getProducts().stream().filter(product -> Objects.equals(product.getProductSerialNumber()
                , "1")).findFirst().orElse(null));
        assertFalse(campaignSaved.isActive());

        Optional<Campaign> opCampFromDB = campaignRepo.findById(responseCampaign.getBody().getId());
        assertTrue(opCampFromDB.isPresent());
        Campaign campaignFromDB = opCampFromDB.get();
        assertEquals(campaignFromDB.getBid(),campaignDTO.getBid());
        assertEquals(campaignFromDB.getName(),campaignDTO.getName());
        assertNotNull(campaignFromDB.getProducts().stream().filter(product -> Objects.equals(product.getProductSerialNumber()
                , "1")).findFirst().orElse(null));
        assertFalse(campaignFromDB.isActive());
    }

    @Test
    @Transactional
    void TestUpdateCampaign(){
        Optional<Campaign> opCampaignB4 = campaignRepo.findById(11L);
        assertTrue(opCampaignB4.isPresent());
        Campaign campaignB4 = opCampaignB4.get();
        LocalDate date = LocalDate.now();
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setId(11L);
        campaignDTO.setBid(200);
        campaignDTO.setName("test-camp");
        campaignDTO.setStartDate(date);
        campaignDTO.addProductSerialNumber("3");

        assertEquals(campaignB4.getId(),campaignDTO.getId());
        assertNotEquals(campaignB4.getBid(),campaignDTO.getBid());
        assertNotEquals(campaignB4.getName(),campaignDTO.getName());
        assertNull(campaignB4.getProducts().stream().filter(product -> Objects.equals(product.getProductSerialNumber()
                , "3")).findFirst().orElse(null));

        ResponseEntity<Campaign> responseCampaign = campaignController.upsert(campaignDTO);
        assertNotNull(responseCampaign.getBody());
        assertEquals(HttpStatus.OK, responseCampaign.getStatusCode());
        Campaign campaignSaved = responseCampaign.getBody();

        assertEquals(campaignSaved.getBid(),campaignDTO.getBid());
        assertEquals(campaignSaved.getName(),campaignDTO.getName());
        assertEquals(campaignSaved.getStartDate(),date);
        assertNotNull(campaignSaved.getProducts().stream().filter(product -> Objects.equals(product.getProductSerialNumber()
                , "3")).findFirst().orElse(null));

        Optional<Campaign> opCampFromDB = campaignRepo.findById(campaignDTO.getId());
        assertTrue(opCampFromDB.isPresent());

        Campaign campaignFromDB = opCampFromDB.get();
        assertEquals(campaignFromDB.getBid(),campaignDTO.getBid());
        assertEquals(campaignFromDB.getName(),campaignDTO.getName());
        assertEquals(campaignFromDB.getStartDate(),campaignDTO.getStartDate());
        assertNotNull(campaignFromDB.getProducts().stream().filter(product -> Objects.equals(product.getProductSerialNumber()
                , "3")).findFirst().orElse(null));
    }

    @Test
    @Transactional
    void TestUpdateCampaignWithPassStartDate(){
        Optional<Campaign> opCampaignB4 = campaignRepo.findById(11L);
        assertTrue(opCampaignB4.isPresent());
        Campaign campaignB4 = opCampaignB4.get();
        assertTrue(campaignB4.isActive());

        LocalDate date = LocalDate.now().minusDays(11);
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setId(11L);
        campaignDTO.setBid(200);
        campaignDTO.setName("test-camp");
        campaignDTO.setStartDate(date);
        campaignDTO.addProductSerialNumber("3");
        campaignDTO.setActive(true);

        assertEquals(campaignB4.getId(),campaignDTO.getId());
        assertNotEquals(campaignB4.getBid(),campaignDTO.getBid());
        assertNotEquals(campaignB4.getName(),campaignDTO.getName());
        assertNull(campaignB4.getProducts().stream().filter(product -> Objects.equals(product.getProductSerialNumber()
                , "3")).findFirst().orElse(null));


        ResponseEntity<Campaign> responseCampaign = campaignController.upsert(campaignDTO);
        assertNotNull(responseCampaign.getBody());
        assertEquals(HttpStatus.OK, responseCampaign.getStatusCode());
        Campaign campaignSaved = responseCampaign.getBody();

        assertEquals(campaignSaved.getBid(),campaignDTO.getBid());
        assertEquals(campaignSaved.getName(),campaignDTO.getName());
        assertEquals(campaignSaved.getStartDate(),date);
        assertNotNull(campaignSaved.getProducts().stream().filter(product -> Objects.equals(product.getProductSerialNumber()
                , "3")).findFirst().orElse(null));
        assertFalse(campaignSaved.isActive());

        Optional<Campaign> opCampFromDB = campaignRepo.findById(campaignDTO.getId());
        assertTrue(opCampFromDB.isPresent());

        Campaign campaignFromDB = opCampFromDB.get();
        assertEquals(campaignFromDB.getBid(),campaignDTO.getBid());
        assertEquals(campaignFromDB.getName(),campaignDTO.getName());
        assertEquals(campaignFromDB.getStartDate(),campaignDTO.getStartDate());
        assertNotNull(campaignFromDB.getProducts().stream().filter(product -> Objects.equals(product.getProductSerialNumber()
                , "3")).findFirst().orElse(null));
        assertFalse(campaignFromDB.isActive());
    }



    @Test
    void TestUpsertWithNotInDBProduct(){
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setId(11L);
        campaignDTO.setBid(500);
        campaignDTO.setName("test-cam");
        campaignDTO.setStartDate(LocalDate.now());
        campaignDTO.addProductSerialNumber("111");

        Exception exception = assertThrows(AppValidationException.class, () -> campaignController.upsert(campaignDTO));
        assertTrue(exception.getMessage().contains(ValidationMsg.NOT_FOUND_PSN));
    }


    @Test
    @Transactional
    void TestGetById() {
        ResponseEntity<Campaign> opFindCampFromDB = campaignController.getById(11L);
        assertNotNull(opFindCampFromDB.getBody());
        assertEquals(HttpStatus.OK, opFindCampFromDB.getStatusCode());
        Campaign campaignFromRes = opFindCampFromDB.getBody();
        assertEquals(11L,campaignFromRes.getId());
        assertEquals("camp-test-1",campaignFromRes.getName());
        assertEquals(400.0,campaignFromRes.getBid());
        assertFalse(campaignFromRes.getProducts().isEmpty());
        Set<String> productPsn = campaignFromRes.getProducts().stream().map(Product::getProductSerialNumber).collect(Collectors.toSet());
        assertTrue(productPsn.contains("1"));
        assertTrue(productPsn.contains("2"));
        assertTrue(productPsn.contains("6"));
    }

    @Test
    void TestGetByIdNotExists() {
        ResponseEntity<Campaign> opNotFindCampFromDB = campaignController.getById(1L);
        assertNull(opNotFindCampFromDB.getBody());
        assertEquals(HttpStatus.NOT_FOUND, opNotFindCampFromDB.getStatusCode());
    }

    @Test
    @Transactional
    void TestDeleteById() {
        Optional<Campaign> campaignB4 = campaignRepo.findById(11L);
        assertTrue(campaignB4.isPresent());
        Set<String> productPsn = campaignB4.get().getProducts().stream().map(Product::getProductSerialNumber).collect(Collectors.toSet());

        campaignController.deleteById(11L);
        Optional<Campaign> campaign = campaignRepo.findById(11L);
        assertFalse(campaign.isPresent());
        assertEquals(productPsn.size(),productRepo.findAllById(productPsn).size());
    }

    @Test
    void TestDeleteIdNotInDB() {
        Exception exception = assertThrows(AppValidationException.class, () -> campaignController.deleteById(6L));
        assertTrue(exception.getMessage().contains(ValidationMsg.NOT_FOUND_ID));
    }

}

