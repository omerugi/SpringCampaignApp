package com.example.mabaya.dto;

import com.example.mabaya.consts.ValidationMsg;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    @Pattern(regexp = "^[a-zA-Z0-9]{1,255}$", message = ValidationMsg.INVALID_PSN)
    private String productSerialNumber;

    @NotNull(message = ValidationMsg.NULL_TITLE)
    @Size(min = 2, max = 25, message = ValidationMsg.SIZE_CONSTRAINT_TITLE_2_25)
    private String title;

    @DecimalMin(value = "0.0", inclusive = true, message = ValidationMsg.NUM_PRICE_NEGATIVE)
    private double price;

    private boolean active = true;

    @NotEmpty(message = ValidationMsg.EMPTY_CATEGORY_NAME)
    private String categoryName;

    @Override
    public String toString() {
        return "ProductDTO{" +
                "productSerialNumber='" + productSerialNumber + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", active=" + active +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
