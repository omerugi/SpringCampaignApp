package com.example.mabaya.repositories;

import com.example.mabaya.entities.Campaign;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CampaignRepo extends CrudRepository<Campaign, Long> {

    @Modifying
    @Query(value = "UPDATE campaign SET active = false WHERE start_date < :date", nativeQuery = true)
    void deactivateOldCampaigns(@Param("date") LocalDate date);

}
