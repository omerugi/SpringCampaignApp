package com.example.mabaya.repositories;

import com.example.mabaya.MabayaApplication;
import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.entities.Campaign;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
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

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = MabayaApplication.class)
@RunWith(SpringRunner.class)
@DataJpaTest
@SqlGroup({@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:schema.sql", "classpath:data.sql"}),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:drop.sql")})
class CampaignRepoTest {

    CampaignRepo campRepo;
    ProductRepo productRepo;
    CategoryRepo categoryRepo;

    @Autowired
    public void setCampRepo(CampaignRepo campRepo) {
        this.campRepo = campRepo;
    }

    @Autowired
    public void setProductRepo(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Autowired
    public void setCategoryRepo(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @PersistenceContext
    EntityManager entityManager;


    private Campaign getNewCampaign(String name) {
        Campaign campaign = new Campaign();
        campaign.setBid(100);
        campaign.setName(name);
        campaign.setStartDate(LocalDate.now());
        campaign.addProduct(productRepo.findById("1").get());
        return campaign;
    }

    @Test
    void TestDeactivateOldCampaignsUpdates() {
        Campaign campWithPassEndDate1 = getNewCampaign("campWithPassEndDate1");
        campWithPassEndDate1.setStartDate(LocalDate.now().minusDays(15));
        Campaign campWithPassEndDate2 = getNewCampaign("campWithPassEndDate2");
        campWithPassEndDate2.setStartDate(LocalDate.now().minusDays(11));

        entityManager.persist(campWithPassEndDate1);
        entityManager.persist(campWithPassEndDate2);
        entityManager.clear();
        entityManager.flush();

        campRepo.deactivateOldCampaigns(LocalDate.now().minusDays(10));
        entityManager.clear();
        entityManager.flush();

        Campaign foundCamp1 = entityManager.find(Campaign.class, campWithPassEndDate1.getId());
        Campaign foundCamp2 = entityManager.find(Campaign.class, campWithPassEndDate2.getId());

        assertFalse(foundCamp1.isActive());
        assertFalse(foundCamp2.isActive());
    }

    @Test
    @Transactional
    void TestDeactivateOldCampaignsNoUpdates() {
        Campaign campWithValidEndDate1 = getNewCampaign("campWithValidEndDate1");
        campWithValidEndDate1.setStartDate(LocalDate.now().minusDays(10));
        Campaign campWithValidEndDate2 = getNewCampaign("campWithValidEndDate2");

        entityManager.persist(campWithValidEndDate1);
        entityManager.persist(campWithValidEndDate2);
        entityManager.clear();
        entityManager.flush();


        campRepo.deactivateOldCampaigns(LocalDate.now().minusDays(10));
        entityManager.clear();
        entityManager.flush();

        Campaign foundCamp1 = entityManager.find(Campaign.class, campWithValidEndDate1.getId());
        Campaign foundCamp2 = entityManager.find(Campaign.class, campWithValidEndDate2.getId());

        assertTrue(foundCamp1.isActive());
        assertTrue(foundCamp2.isActive());
    }

    @Test
    void testFindCampaignById() {
        Long idToFind = 11L;
        Optional<Campaign> campaignFound = campRepo.findById(idToFind);
        assertTrue(campaignFound.isPresent());
        assertEquals(idToFind, campaignFound.get().getId());
    }

    @Test
    void TestSaveCampaignSuccesses() {
        Campaign campaignToSave = getNewCampaign("test");
        campRepo.saveAndFlush(campaignToSave);

        Campaign foundCampaign = entityManager.find(Campaign.class, campaignToSave.getId());
        assertEquals(campaignToSave.getId(), foundCampaign.getId());
    }

    @Test
    void TestSaveCampaignFailNullStartDate() {
        Campaign campaignNullDate = getNewCampaign("nullDate");
        campaignNullDate.setStartDate(null);

        ConstraintViolationException thrown = assertThrows(
                ConstraintViolationException.class,
                () -> campRepo.saveAndFlush(campaignNullDate));
        assertTrue(thrown.getMessage().contains(ValidationMsg.NULL_START_DATE));
    }

    @Test
    void TestSaveCampaignFailShortName() {
        Campaign campaignShortName = getNewCampaign("s");
        ConstraintViolationException thrownShortName = assertThrows(
                ConstraintViolationException.class,
                () -> campRepo.saveAndFlush(campaignShortName));
        assertTrue(thrownShortName.getMessage().contains(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25));
    }

    @Test
    void TestSaveCampaignFailLongName() {
        Campaign campaignLongName = getNewCampaign("this is a really long name to test");
        ConstraintViolationException thrownLongName = assertThrows(
                ConstraintViolationException.class,
                () -> campRepo.saveAndFlush(campaignLongName));
        assertTrue(thrownLongName.getMessage().contains(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25));
    }

    @Test
    void TestSaveCampaignFailEmptyName() {
        Campaign campaignEmptyName = getNewCampaign("");
        ConstraintViolationException thrownShortName = assertThrows(
                ConstraintViolationException.class,
                () -> campRepo.saveAndFlush(campaignEmptyName));
        assertTrue(thrownShortName.getMessage().contains(ValidationMsg.SIZE_CONSTRAINT_NAME_2_25));
    }

    @Test
    void TestSaveCampaignFailNullName() {
        Campaign campaignNullName = getNewCampaign(null);
        campaignNullName.setName(null);
        ConstraintViolationException nullLongName = assertThrows(
                ConstraintViolationException.class,
                () -> campRepo.saveAndFlush(campaignNullName));
        assertTrue(nullLongName.getMessage().contains(ValidationMsg.NULL_NAME));
    }

    @Test
    void TestSaveCampaignFailNegativeBid() {
        Campaign campaign = getNewCampaign("negativeBid");
        campaign.setBid(-1L);

        ConstraintViolationException thrownNullName = assertThrows(
                ConstraintViolationException.class,
                () ->campRepo.saveAndFlush(campaign));
        assertTrue(thrownNullName.getMessage().contains(ValidationMsg.NUM_BID_NEGATIVE));
    }

    // TODO: Test delete

}
