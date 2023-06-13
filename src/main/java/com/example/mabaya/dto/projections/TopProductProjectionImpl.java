package com.example.mabaya.dto.projections;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopProductProjectionImpl implements TopProductProjection{
    private Double bid;
    private String product_serial_number;
    private String title;
    private String category;
    private Double price;
}
