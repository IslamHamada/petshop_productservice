package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.dto.ProductDTO;
import com.islamhamada.petshop.entity.Product;
import com.islamhamada.petshop.exception.ProductServiceException;
import com.islamhamada.petshop.model.ProductRequest;
import com.islamhamada.petshop.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductDTO> getAllProducts() {
        log.info("Getting all products");
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = products.stream().map(product -> {
            ProductDTO productDTO = ProductDTO.builder()
                    .name(product.getName())
                    .id(product.getId())
                    .price(product.getPrice())
                    .image(product.getImage())
                    .description(product.getDescription())
                    .for_animal(product.getForAnimal())
                    .utility(product.getUtility())
                    .quantity(product.getQuantity())
                    .build();
            return productDTO;
        }).toList();
        log.info("All products fetched successfully");
        return productDTOS;
    }

    @Override
    public ProductDTO createProduct(ProductRequest productRequest) {
        log.info("Creating product with name: " + productRequest.getName());
        if(productRepository.findByName(productRequest.getName()).isPresent())
            throw new ProductServiceException(
                    "A product already exists with name: " + productRequest.getName(),
                    "NAME_ALREADY_TAKEN", HttpStatus.CONFLICT);
        Product product = Product.builder()
                .name(productRequest.getName())
                .build();
        Product rv = productRepository.save(product);
        ProductDTO productDTO = ProductDTO.builder()
                        .name(productRequest.getName())
                        .build();
        log.info("Product successfully created with name: " + productRequest.getName() + " and id: " + rv.getId());
        return productDTO;
    }

    @Override
    public ProductDTO getProductById(long id) {
        log.info("Getting product by id: " + id);
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ProductServiceException("Product not found with id: " + id, "NOT_FOUND", HttpStatus.NOT_FOUND)
        );
        log.info("Product with id: " + id + " successfully fetched");
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .quantity(product.getQuantity())
                .description(product.getDescription())
                .image(product.getImage())
                .for_animal(product.getForAnimal())
                .utility(product.getUtility())
                .price(product.getPrice())
                .build();
    }

    @Override
    public void reduceProductQuantity(long product_id, int amount) {
        log.info("Reducing amount of product with id: " + product_id + "by: " + amount);
        Product product = productRepository.findById(product_id).orElseThrow(() ->
                new ProductServiceException("Product not found with id: " + product_id, "NOT_FOUND", HttpStatus.NOT_FOUND)
        );
        if(amount > product.getQuantity())
            throw new ProductServiceException(
                    "A product can't have a negative quantity. Amount of " + amount + " is too high for product with id: " + product_id,
                    "QUANTITY_ERROR", HttpStatus.CONFLICT);
        product.setQuantity(product.getQuantity() - amount);
        productRepository.save(product);
        log.info("Product's amount with id: " + product_id + " successfully reduced by " + amount);
    }

    @Override
    public List<String> getUtilities() {
        log.info("Getting utilities");
        List<String> rv = productRepository.getDistinctUtilities();
        log.info("Utilities successfully fetched");
        return rv;
    }

    @Override
    public List<String> getForAnimals() {
        log.info("Getting for_animals");
        List<String> rv = productRepository.getDistinctForAnimals();
        log.info("for_animals successfully fetched");
        return rv;
    }
}
