package com.islamhamada.petshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Builder
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private int quantity;
}
