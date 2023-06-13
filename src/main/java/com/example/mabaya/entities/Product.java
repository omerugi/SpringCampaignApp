package com.example.mabaya.entities;

import com.example.mabaya.consts.ValidationMsg;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.*;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "product_serial_number", nullable = false)
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = ValidationMsg.INVALID_PSN)
    private String productSerialNumber;

    @Column(name = "title", nullable = false, unique = true)
    @Size(min = 2, max = 25, message = ValidationMsg.SIZE_CONSTRAINT_TITLE_2_25)
    @NotNull(message = ValidationMsg.NULL_TITLE)
    private String title;

    @Column(name = "price", nullable = false)
    @DecimalMin(value = "0.0", inclusive = true, message = ValidationMsg.NUM_PRICE_NEGATIVE)
    private double price;

    @Column(name = "active", columnDefinition = "boolean default true")
    private boolean active = true;

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Campaign> campaigns = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = ValidationMsg.NULL_CATEGORY)
    private Category category;

}
