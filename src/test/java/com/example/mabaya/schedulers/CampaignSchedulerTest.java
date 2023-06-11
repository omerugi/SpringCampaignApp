package com.example.mabaya.schedulers;

import com.example.mabaya.MabayaApplication;
import com.example.mabaya.repositories.CampaignRepo;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
class CampaignSchedulerTest {

    @Autowired
    CampaignScheduler campaignScheduler;

    @MockBean
    private CampaignRepo campaignRepo;

    @Test
    void testDeactivateOldCampaigns() throws InterruptedException {
        Thread.sleep(2000L);
        verify(campaignRepo, times(2)).deactivateOldCampaigns(any(LocalDate.class));;
    }
}
