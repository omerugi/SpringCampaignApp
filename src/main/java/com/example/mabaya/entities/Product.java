package com.example.mabaya.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.*;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "product_serial_number", nullable = false)
    @NotEmpty(message = "Product Serial Number cannot be empty")
    private String productSerialNumber;

    @Column(name = "title", nullable = false, unique = true)
    @Size(min = 2, max = 25, message = "Title should be between 2-25 chars")
    private String title;

    @Column(name = "price", nullable = false)
    @DecimalMin(value = "0.0", inclusive = true)
    private double price;

    @Column(name = "active", columnDefinition = "boolean default true")
    private boolean active = true;

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Campaign> campaigns = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Must have a category")
    private Category category;

}
