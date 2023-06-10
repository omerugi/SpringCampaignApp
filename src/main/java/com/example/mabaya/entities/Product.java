package com.example.mabaya.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "Title cannot be empty")
    private String title;


    // TODO: Maybe could be one to many
    @Column(name = "category", nullable = false)
    @NotEmpty(message = "Category cannot be empty")
    private String category;

    @Column(name = "price", nullable = false)
    @DecimalMin(value = "0.0", inclusive = true)
    private double price;

    @Column(name = "active", columnDefinition = "boolean default true")
    private boolean active = true;

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Campaign> campaigns = new HashSet<>();

}
