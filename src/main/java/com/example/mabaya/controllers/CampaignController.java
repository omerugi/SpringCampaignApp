package com.example.mabaya.controllers;

import com.example.mabaya.dto.CampaignDTO;
import com.example.mabaya.entities.Campaign;
import com.example.mabaya.schedulers.CampaignScheduler;
import com.example.mabaya.servises.interfaces.CampaignService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/campaign")
public class CampaignController {

    @Autowired
    CampaignService campaignService;

    @Autowired
    CampaignScheduler campaignScheduler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Campaign createCampaign(@RequestBody @Valid CampaignDTO newCampaign){
        return campaignService.createUpdateCampaign(newCampaign);
    }

    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateStartDate(){
        campaignScheduler.deactivateOldCampaigns();
    }
}
