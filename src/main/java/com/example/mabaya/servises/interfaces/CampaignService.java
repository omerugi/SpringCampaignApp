package com.example.mabaya.servises.interfaces;

import com.example.mabaya.dto.CampaignDTO;
import com.example.mabaya.entities.Campaign;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CampaignService {

    Campaign upsert(CampaignDTO campaign);

    Optional<Campaign> getById(Long id);

    void deleteById(Long id);

    List<Campaign> getAll();
}
