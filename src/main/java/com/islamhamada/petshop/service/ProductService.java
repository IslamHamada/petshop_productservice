package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.dto.ProductDTO;
import com.islamhamada.petshop.entity.Product;
import com.islamhamada.petshop.model.ProductRequest;
import com.islamhamada.petshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    Product createProduct(ProductRequest productRequest);
    ProductDTO getProductById(long id);
    void reduceProductQuantity(long product_id, int amount);
    List<String> getUtilities();
    List<String> getForAnimals();
}
