package com.islamhamada.petshop.repository;

import com.islamhamada.petshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select distinct utility from Product")
    public List<String> getDistinctUtilities();

    @Query("select distinct forAnimal from Product")
    public List<String> getDistinctForAnimals();
}
