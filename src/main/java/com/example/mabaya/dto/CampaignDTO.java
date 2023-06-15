package com.example.mabaya.dto;

import com.example.mabaya.consts.ValidationMsg;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CampaignDTO {

    private Long id;

    @Size(min = 2, max = 25, message = ValidationMsg.SIZE_CONSTRAINT_NAME_2_25)
    @NotNull(message = ValidationMsg.NULL_NAME)
    private String name;

    @NotNull(message = ValidationMsg.NULL_START_DATE)
    private LocalDate startDate;

    @DecimalMin(value = "0.0", inclusive = true, message = ValidationMsg.NUM_BID_NEGATIVE)
    private double bid;

    private boolean active = true;

    @NotEmpty(message = ValidationMsg.EMPTY_PSN)
    private Set<@Valid @Pattern(regexp="^[a-zA-Z0-9]{1,255}$", message = ValidationMsg.INVALID_PSN)String> productSerialNumbers = new HashSet<>();

    public void addProductSerialNumber(String productSerialNumber){
        productSerialNumbers.add(productSerialNumber);
    }

    @Override
    public String toString() {
        return "CampaignDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", bid=" + bid +
                ", active=" + active +
                ", productSerialNumbers=" + productSerialNumbers +
                '}';
    }
}
