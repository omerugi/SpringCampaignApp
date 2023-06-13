package com.example.mabaya.entities;

import com.example.mabaya.consts.ValidationMsg;
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

    private Campaign getCampaign(String name) {
        Campaign campaign = new Campaign();
        campaign.setName(name);
        campaign.setStartDate(LocalDate.now());
        return campaign;
    }

    @Test
    void TestEmptyNameConstraintViolation() {
        Campaign campaignEmptyName = getCampaign("");
        Set<ConstraintViolation<Campaign>> violationsEmptyName = validator.validate(campaignEmptyName);
        assertFalse(violationsEmptyName.isEmpty());
        assertEquals(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25, violationsEmptyName.iterator().next().getMessage());
    }

    @Test
    void TestShortNameConstraintViolation() {
        Campaign campaignShortName = getCampaign("1");
        Set<ConstraintViolation<Campaign>> violationsShortName = validator.validate(campaignShortName);
        assertFalse(violationsShortName.isEmpty());
        assertEquals(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25, violationsShortName.iterator().next().getMessage());
    }

    @Test
    void TestLongNameConstraintViolation() {
        Campaign campaignLongName = getCampaign("111111111111111111111111111111");
        Set<ConstraintViolation<Campaign>> violationsLongName = validator.validate(campaignLongName);
        assertFalse(violationsLongName.isEmpty());
        assertEquals(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25, violationsLongName.iterator().next().getMessage());
    }

    @Test
    void TestNameNullConstraintViolation() {
        Campaign campaign = getCampaign(null);
        Set<ConstraintViolation<Campaign>> violations = validator.validate(campaign);
        assertFalse(violations.isEmpty());
        assertEquals(ValidationMsg.NULL_NAME, violations.iterator().next().getMessage());
    }

    @Test
    void TestBidNegativeConstraintViolation() {
        Campaign campaign = getCampaign("testcamp");
        campaign.setBid(-10.0);
        Set<ConstraintViolation<Campaign>> violations = validator.validate(campaign);
        assertFalse(violations.isEmpty());
        assertEquals(ValidationMsg.NUM_BID_NEGATIVE, violations.iterator().next().getMessage());
    }

    @Test
    void TestStartDateNullConstraintViolation() {
        Campaign campaign = getCampaign("testcamp");
        campaign.setStartDate(null);
        Set<ConstraintViolation<Campaign>> violations = validator.validate(campaign);
        assertFalse(violations.isEmpty());
        assertEquals(ValidationMsg.NULL_START_DATE, violations.iterator().next().getMessage());
    }
}
