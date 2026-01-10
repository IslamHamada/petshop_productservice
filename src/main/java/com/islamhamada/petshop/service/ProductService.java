package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.dto.ProductDTO;
import com.islamhamada.petshop.model.ProductRequest;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    ProductDTO createProduct(ProductRequest productRequest);
    ProductDTO getProductById(long id);
    int reduceProductQuantity(long product_id, int amount);
    List<String> getUtilities();
    List<String> getForAnimals();
}
