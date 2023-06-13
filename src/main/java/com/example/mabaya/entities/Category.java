package com.example.mabaya.entities;


import com.example.mabaya.consts.ValidationMsg;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="category")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "cat_id")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @Size(min = 2, max = 25, message =ValidationMsg.SIZE_CONSTRAINT_NAME_2_25)
    @NotNull(message =  ValidationMsg.NULL_NAME)
    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Product> products = new HashSet<>();

    public void addProduct(Product product){
        products.add(product);
    }
}
