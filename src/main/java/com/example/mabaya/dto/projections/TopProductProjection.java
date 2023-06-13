package com.example.mabaya.dto.projections;

public interface TopProductProjection {

    Double getBid();
    String getProduct_serial_number();
    String getTitle();
    String getCategory();
    Double getPrice();

    void setBid(Double bid);
    void setProduct_serial_number(String product_serial_number);
    void setTitle(String title);
    void setCategory(String category);
    void setPrice(Double price);

}
