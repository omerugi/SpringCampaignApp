package com.example.mabaya.repositories;

import com.example.mabaya.MabayaApplication;
import com.example.mabaya.entities.Campaign;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = MabayaApplication.class)
@RunWith(SpringRunner.class)
@DataJpaTest
@SqlGroup({@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:schema.sql", "classpath:data.sql"}),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:drop.sql")})
class CampaignRepoTest {

    @Autowired
    CampaignRepo campRepo;

    @Autowired
    ProductRepo productRepo;

    @PersistenceContext
    EntityManager entityManager;

    private void clearAndFlush(){
        entityManager.clear();
        entityManager.flush();
    }

    private Campaign getNewCampaign(String name) {
        Campaign campaign = new Campaign();
        campaign.setBid(100);
        campaign.setName(name);
        campaign.setStartDate(LocalDate.now());
        campaign.addProduct(productRepo.findById("1").get());
        return campaign;
    }

    // TODO: Split the tests
    @Test
    @Transactional
    void TestDeactivateOldCampaigns() {
        Campaign campWithPassEndDate1 = getNewCampaign("campWithPassEndDate1");
        campWithPassEndDate1.setStartDate(LocalDate.now().minusDays(15));
        Campaign campWithPassEndDate2 = getNewCampaign("campWithPassEndDate2");
        campWithPassEndDate2.setStartDate(LocalDate.now().minusDays(11));
        Campaign campWithValidEndDate1 = getNewCampaign("campWithValidEndDate1");
        campWithValidEndDate1.setStartDate(LocalDate.now().minusDays(10));
        Campaign campWithValidEndDate2 = getNewCampaign("campWithValidEndDate2");

        List<Long> savedCampIds = StreamSupport.stream(
                        campRepo.saveAll(
                                Arrays.asList(campWithPassEndDate1, campWithPassEndDate2, campWithValidEndDate1,campWithValidEndDate2)).spliterator()
                        , false)
                .map(Campaign::getId).toList();
        clearAndFlush();
        campRepo.deactivateOldCampaigns(LocalDate.now().minusDays(10));
        clearAndFlush();

        Map<String, Boolean> afterUpdateCamps = StreamSupport.stream(
                        campRepo.findAllById(savedCampIds).spliterator()
                        , false)
                .collect(Collectors.toMap(Campaign::getName,Campaign::isActive));

        assertFalse(afterUpdateCamps.get("campWithPassEndDate1"));
        assertFalse(afterUpdateCamps.get("campWithPassEndDate2"));
        assertTrue(afterUpdateCamps.get("campWithValidEndDate1"));
        assertTrue(afterUpdateCamps.get("campWithValidEndDate2"));
    }

    @Test
    void TestSaveCampaign() {
        Campaign campaignToSave = getNewCampaign("test");
        campRepo.save(campaignToSave);

        clearAndFlush();
        Campaign foundCampaign = entityManager.find(Campaign.class, campaignToSave.getId());

        assertEquals(campaignToSave.getId(), foundCampaign.getId());
    }

    @Test
    void testFindCampaignById() {
        Long idToFind = 11L;
        Optional<Campaign> campaignFound = campRepo.findById(idToFind);

        assertTrue(campaignFound.isPresent());
        assertEquals(11L, campaignFound.get().getId());
    }



}
