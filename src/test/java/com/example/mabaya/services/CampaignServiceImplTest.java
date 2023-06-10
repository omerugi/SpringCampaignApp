package com.example.mabaya.services;

import com.example.mabaya.repositories.CampaignRepo;
import com.example.mabaya.dto.CampaignDTO;
import com.example.mabaya.entities.Campaign;
import com.example.mabaya.entities.Product;
import com.example.mabaya.servises.impls.CampaignServiceImpl;
import com.example.mabaya.servises.interfaces.ProductService;
import com.example.mabaya.utils.CampaignUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class CampaignServiceImplTest {

    @Mock
    ProductService productService;
    @Mock
    CampaignRepo campaignRepo;
    @InjectMocks
    CampaignServiceImpl campaignService;


    private Product getNewProduct(String title, String psc){
        Product product = new Product();
        product.setCategory("test-cat");
        product.setProductSerialNumber(psc);
        product.setTitle(title);
        product.setPrice(10);
        return product;
    }

    private Campaign getNewCampaign(String name) {
        Campaign campaign = new Campaign();
        campaign.setBid(100);
        campaign.setName(name);
        campaign.setStartDate(LocalDate.now());
        return campaign;
    }

    @Test
    void TestCreateCampaignSuccesses(){
        Campaign savedCamp = getNewCampaign("test-camp");
        Set<Product> productOfSavedCamp = new HashSet<>(List.of(getNewProduct("test-prod", "test-prod-1")));
        savedCamp.setProducts(productOfSavedCamp);
        CampaignDTO campaignToSaveDTO = CampaignUtils.getCampaignDTOFromCampaign(savedCamp);
        when(campaignRepo.save(any())).thenReturn(savedCamp);
        when(productService.getProductFromSerialNumbers(any())).thenReturn(productOfSavedCamp);

        Campaign afterSaveCamp = campaignService.createUpdateCampaign(campaignToSaveDTO);

        assertSame(afterSaveCamp,savedCamp);
    }

    @Test
    void TestCreateCampaignWithEmptyProductSet(){
        Campaign savedCamp = getNewCampaign("test-camp");
        CampaignDTO campaignToSaveDTO = CampaignUtils.getCampaignDTOFromCampaign(savedCamp);
        Exception exception = assertThrows(IllegalArgumentException.class,
                ()->campaignService.createUpdateCampaign(campaignToSaveDTO));
        assertTrue(exception.getMessage().contains("Product list cannot be empty"));
    }


}
