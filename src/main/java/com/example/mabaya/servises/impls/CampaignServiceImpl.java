package com.example.mabaya.servises.impls;

import com.example.mabaya.consts.ValidationMsg;
import com.example.mabaya.exeption.AppValidationException;
import com.example.mabaya.repositories.CampaignRepo;
import com.example.mabaya.dto.CampaignDTO;
import com.example.mabaya.entities.Campaign;
import com.example.mabaya.entities.Product;
import com.example.mabaya.servises.interfaces.CampaignService;
import com.example.mabaya.servises.interfaces.ProductService;
import com.example.mabaya.utils.CampaignUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private CampaignRepo campaignRepo;

    @Autowired
    private ProductService productService;


    @Transactional
    @Override
    public Campaign upsert(CampaignDTO campaignDTO) {
        Campaign campaign = createCampaignFromDTO(campaignDTO);
        return campaignRepo.save(campaign);
    }

    private Campaign createCampaignFromDTO(CampaignDTO campaignDTO) {
        if (campaignDTO.getProductSerialNumbers().isEmpty()) {
            throw new AppValidationException(ValidationMsg.EMPTY_PSN);
        }
        Set<Product> products = productService.getProductsBySerialNumbers(campaignDTO.getProductSerialNumbers());
        return CampaignUtils.getCampaignFromCampaignDTO(campaignDTO, products);
    }

    @Override
    public Optional<Campaign> getById(Long id) {
        return campaignRepo.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        Optional<Campaign> opCampaignFromDB = getById(id);
        Campaign campaign = opCampaignFromDB.orElseThrow(() -> new AppValidationException(id+" "+ValidationMsg.NOT_FOUND_ID));
        campaignRepo.delete(campaign);
    }
}
