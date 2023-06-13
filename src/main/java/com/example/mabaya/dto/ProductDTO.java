package com.example.mabaya.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Product Serial Number should be at lease size 1 and contain letters and digits")
    String productSerialNumber;

    @Size(min = 2, max = 25, message = "Title should be between 2-25 chars")
    private String title;

    @DecimalMin(value = "0.0", inclusive = true)
    private double price;

    private boolean active = true;

    @NotEmpty(message = "Category name cannot be empty")
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
