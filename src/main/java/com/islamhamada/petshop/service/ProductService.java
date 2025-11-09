package com.islamhamada.petshop.service;

import com.islamhamada.petshop.entity.Product;
import com.islamhamada.petshop.model.ProductRequest;
import com.islamhamada.petshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();
    Product createProduct(ProductRequest productRequest);
}
