package com.islamhamada.petshop.controller;

import com.islamhamada.petshop.entity.Product;
import com.islamhamada.petshop.model.ProductRequest;
import com.islamhamada.petshop.model.ReduceQuantityRequest;
import com.islamhamada.petshop.service.ProductService;
import com.islamhamada.petshop.contracts.dto.ProductDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @PreAuthorize("hasAnyRole('Admin')")
    @PostMapping
    public String createProduct(@Valid @RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest).toString();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") long id){
        ProductDTO product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PutMapping("/{product_id}")
    public ResponseEntity<Void> reduceProductQuantity(@PathVariable("product_id") long product_id, @Valid @RequestBody ReduceQuantityRequest request) {
        productService.reduceProductQuantity(product_id, request.getAmount());
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/utilities")
    public ResponseEntity<List<String>> getUtilities(){
        List<String> utilities = productService.getUtilities();
        return new ResponseEntity<>(utilities, HttpStatus.OK);
    }

}
