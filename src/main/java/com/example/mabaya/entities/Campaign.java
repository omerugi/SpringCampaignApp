package com.example.mabaya.entities;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "campaign")
@NamedEntityGraph(
        name = "campaign-products",
        attributeNodes = @NamedAttributeNode("products")
)
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "camp_id")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @Column(name = "bid", nullable = false)
    @DecimalMin(value = "0.0", inclusive = true)
    private double bid;

    @Column(name = "active", columnDefinition = "boolean  default true")
    private boolean active = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_campaign",
            joinColumns = @JoinColumn(name = "campaign_id"),
            inverseJoinColumns = @JoinColumn(name = "product_serial_number"))
    private Set<Product> products = new HashSet<>();

    public void addProduct(@NonNull Product newProduct){
        this.products.add(newProduct);
    }
}