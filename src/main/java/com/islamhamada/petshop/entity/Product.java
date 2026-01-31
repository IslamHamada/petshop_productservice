package com.islamhamada.petshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity(name = "product")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String name;

    private int quantity;

    private BigDecimal price;

    private String description;

    private String image;

    @Column(name = "for_animal")
    private String forAnimal;

    private String utility;
}
