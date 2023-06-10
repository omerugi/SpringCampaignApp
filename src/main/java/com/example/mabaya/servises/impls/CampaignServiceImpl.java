package com.example.mabaya.servises.impls;

import com.example.mabaya.repositories.CampaignRepo;
import com.example.mabaya.dto.CampaignDTO;
import com.example.mabaya.entities.Campaign;
import com.example.mabaya.entities.Product;
import com.example.mabaya.servises.interfaces.CampaignService;
import com.example.mabaya.servises.interfaces.ProductService;
import com.example.mabaya.utils.CampaignUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    CampaignRepo campaignRepo;

    @Autowired
    ProductService productService;

    @Transactional
    @Override
    public Campaign createUpdateCampaign(CampaignDTO campaignDTO) {
        Campaign campaign = createCampaignFromDTO(campaignDTO);
        return campaignRepo.save(campaign);
    }

    private Campaign createCampaignFromDTO(CampaignDTO campaignDTO) {
        if (campaignDTO.getProductSerialNumbers().isEmpty()) {
            throw new IllegalArgumentException("Product list cannot be empty");
        }
        Set<Product> products = productService.getProductFromSerialNumbers(campaignDTO.getProductSerialNumbers());
        return CampaignUtils.getCampaignFromCampaignDTO(campaignDTO, products);
    }


}
