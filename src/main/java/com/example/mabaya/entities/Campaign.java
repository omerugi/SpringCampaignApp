package com.example.mabaya.entities;

import com.example.mabaya.consts.ValidationMsg;
import jakarta.validation.constraints.*;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "campaign")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "camp_id")
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(min = 2, max = 25, message = ValidationMsg.SIZE_CONSTRAINT_NAME_2_25)
    @NotNull(message = ValidationMsg.NULL_NAME)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull(message = ValidationMsg.NULL_START_DATE)
    private LocalDate startDate;

    @Column(name = "bid", nullable = false)
    @DecimalMin(value = "0.0", inclusive = true, message = ValidationMsg.NUM_BID_NEGATIVE)
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