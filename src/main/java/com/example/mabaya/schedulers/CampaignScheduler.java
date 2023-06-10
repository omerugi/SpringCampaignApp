package com.example.mabaya.schedulers;

import com.example.mabaya.repositories.CampaignRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class CampaignScheduler {

    @Autowired
    CampaignRepo campaignRepo;

    @Scheduled(cron = "${app.scheduler.time.test}")
    @Transactional
    public void deactivateOldCampaigns() {
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
        campaignRepo.deactivateOldCampaigns(tenDaysAgo);
    }
}
