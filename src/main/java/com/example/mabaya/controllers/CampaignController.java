package com.example.mabaya.controllers;

import com.example.mabaya.dto.CampaignDTO;
import com.example.mabaya.entities.Campaign;
import com.example.mabaya.schedulers.CampaignScheduler;
import com.example.mabaya.servises.interfaces.CampaignService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/campaign")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CampaignScheduler campaignScheduler;

    @PostMapping
    public ResponseEntity<Campaign> upsert(@RequestBody @Valid CampaignDTO campaignDTO){
        Campaign campaignFromDB = campaignService.upsert(campaignDTO);
        return new ResponseEntity<>(campaignFromDB, campaignFromDB.getId().equals(campaignDTO.getId())?
                HttpStatus.OK:
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Campaign> getById(@PathVariable("id") Long id){
        Optional<Campaign> campaignFromDB = campaignService.getById(id);
        return campaignFromDB.map(campaign -> new ResponseEntity<>(campaign, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable("id") Long id){
        campaignService.deleteById(id);
    }

}
