package com.example.mabaya.servises.interfaces;

import com.example.mabaya.dto.CampaignDTO;
import com.example.mabaya.entities.Campaign;
import org.springframework.stereotype.Service;

@Service
public interface CampaignService {

    Campaign createUpdateCampaign(CampaignDTO campaign);

}
