package com.islamhamada.petshop.controller;

import com.islamhamada.petshop.entity.Product;
import com.islamhamada.petshop.model.ProductRequest;
import com.islamhamada.petshop.service.ProductService;
import com.islamhamada.petshop.contracts.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping
    public Product createProduct(@RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") long id){
        ProductDTO product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PutMapping("/{product_id}")
    public ResponseEntity<Void> reduceProductQuantity(@PathVariable("product_id") long product_id, @RequestBody ReduceQuantityRequest request) {
        productService.reduceProductQuantity(product_id, request.getAmount());
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
