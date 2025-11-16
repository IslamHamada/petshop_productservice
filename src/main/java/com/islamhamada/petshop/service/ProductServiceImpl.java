package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.ProductDTO;
import com.islamhamada.petshop.entity.Product;
import com.islamhamada.petshop.exception.ProductServiceException;
import com.islamhamada.petshop.model.ProductRequest;
import com.islamhamada.petshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .build();
        return productRepository.save(product);
    }

    @Override
    public ProductDTO getProductById(long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ProductServiceException("No product with given id: " + id
                        , 404, HttpStatus.NOT_FOUND));
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .quantity(product.getQuantity())
                .build();
    }

    @Override
    public void reduceProductQuantity(long product_id, int amount) {
        Product product = productRepository.findById(product_id).get();
        product.setQuantity(product.getQuantity() - amount);
        productRepository.save(product);
    }
}
