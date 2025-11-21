package com.islamhamada.petshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private int quantity;

    private double price;

    private String description;

    private String image;

    @Column(name = "for_animal")
    private String forAnimal;

    private String utility;
}
