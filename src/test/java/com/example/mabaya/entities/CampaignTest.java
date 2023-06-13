package com.example.mabaya.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CampaignTest {
    @Autowired
    private Validator validator;

    @Test
    void TestNameIsEmptyConstraintViolation() {
        Campaign campaign = new Campaign();
        campaign.setName("");
        campaign.setStartDate(LocalDate.now());
        campaign.setBid(10.0);
        Set<ConstraintViolation<Campaign>> violations = validator.validate(campaign);
        assertFalse(violations.isEmpty());
        assertEquals("Name should be between 2-25 chars", violations.iterator().next().getMessage());
    }

    @Test
    void TestBidIsNegativeConstraintViolation() {
        Campaign campaign = new Campaign();
        campaign.setName("Test Campaign");
        campaign.setStartDate(LocalDate.now());
        campaign.setBid(-10.0);
        Set<ConstraintViolation<Campaign>> violations = validator.validate(campaign);
        assertFalse(violations.isEmpty());
        assertEquals("must be greater than or equal to 0.0", violations.iterator().next().getMessage());
    }

    @Test
    void TestStartDateIsNullConstraintViolation() {
        Campaign campaign = new Campaign();
        campaign.setName("Test Campaign");
        campaign.setBid(10.0);
        Set<ConstraintViolation<Campaign>> violations = validator.validate(campaign);
        assertFalse(violations.isEmpty());
        assertEquals("Start date cannot be null", violations.iterator().next().getMessage());
    }
}
