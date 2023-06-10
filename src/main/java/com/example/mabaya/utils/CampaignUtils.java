package com.example.mabaya.utils;

import com.example.mabaya.dto.CampaignDTO;
import com.example.mabaya.entities.Campaign;
import com.example.mabaya.entities.Product;

import java.util.Set;
import java.util.stream.Collectors;

public class CampaignUtils {
    private CampaignUtils(){}

    public static Campaign getCampaignFromCampaignDTO(CampaignDTO campaignDTO, Set<Product> products) {
        Campaign newCamp = new Campaign();
        newCamp.setId(campaignDTO.getId());
        newCamp.setName(campaignDTO.getName());
        newCamp.setStartDate(campaignDTO.getStartDate());
        newCamp.setBid(campaignDTO.getBid());
        newCamp.setProducts(products);
        return newCamp;
    }

    public static CampaignDTO getCampaignDTOFromCampaign(Campaign campaign){
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setId(campaign.getId());
        campaignDTO.setActive(campaignDTO.isActive());
        campaignDTO.setName(campaignDTO.getName());
        campaignDTO.setBid(campaignDTO.getBid());
        campaignDTO.setStartDate(campaign.getStartDate());
        campaignDTO.setProductSerialNumbers(campaign.getProducts().stream().map(Product::getProductSerialNumber).collect(Collectors.toSet()));
        return campaignDTO;
    }
}
