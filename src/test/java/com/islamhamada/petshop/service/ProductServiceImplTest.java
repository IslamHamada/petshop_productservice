package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.dto.ProductDTO;
import com.islamhamada.petshop.entity.Product;
import com.islamhamada.petshop.exception.ProductServiceException;
import com.islamhamada.petshop.model.ProductRequest;
import com.islamhamada.petshop.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    ProductService productService = new ProductServiceImpl();

    @Nested
    @DisplayName("getAllProducts()")
    class getAllProducts {
        @DisplayName("success")
        @Test
        void getAllProducts_success() {
            List<Product> products = getMockProductList();

            when(productRepository.findAll())
                    .thenReturn(products);

            List<ProductDTO> rv = productService.getAllProducts();

            verify(productRepository, times(1)).findAll();

            assertEquals(rv.size(), products.size());
            for(int i = 0; i < rv.size(); i++){
                ProductDTO productDTO = rv.get(i);
                Product product = products.get(i);
                assertEquals(productDTO.getId(), product.getId());
                assertEquals(productDTO.getName(), product.getName());
                assertEquals(productDTO.getQuantity(), product.getQuantity());
                assertEquals(productDTO.getPrice(), product.getPrice());
                assertEquals(productDTO.getDescription(), product.getDescription());
                assertEquals(productDTO.getImage(), product.getImage());
                assertEquals(productDTO.getUtility(), product.getUtility());
                assertEquals(productDTO.getFor_animal(), product.getForAnimal());
            }
        }
    }

    @Nested
    @DisplayName("createProduct")
    class createProduct {
        @DisplayName("success")
        @Test
        void getProductById_success() {
            Product mockProduct = getMockProduct();
            ProductRequest productRequest = new ProductRequest(mockProduct.getName());

            assertEquals(mockProduct.getName(), productRequest.getName());
            when(productRepository.findByName(anyString()))
                    .thenReturn(Optional.empty());
            when(productRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Product product = productService.createProduct(productRequest);

            verify(productRepository, times(1)).findByName(any());
            verify(productRepository, times(1)).save(any());

            assertEquals(product.getName(), productRequest.getName());
        }

        @DisplayName("failure")
        @Test
        void getProductById_failure() {
            Product mockProduct = getMockProduct();

            when(productRepository.findByName(anyString()))
                    .thenReturn(Optional.of(mockProduct));

            ProductRequest productRequest = new ProductRequest(mockProduct.getName());
            ProductServiceException exception = assertThrows(ProductServiceException.class,
                    () -> productService.createProduct(productRequest));

            verify(productRepository, times(1)).findByName(anyString());
            verify(productRepository, times(0)).save(any());

            assertEquals(exception.getMessage(), "A product already exists with name: " + mockProduct.getName());
            assertEquals(exception.getError_code(), "PRODUCT_NAME_ALREADY_TAKEN");
            assertEquals(exception.getHttpStatus(), HttpStatus.CONFLICT);
        }
    }

    @Nested
    @DisplayName("getProductById")
    class getProductById {
        @DisplayName("success")
        @Test
        void getProductById_success() {
            Product mockProduct = getMockProduct();
            when(productRepository.findById(anyLong()))
                    .thenReturn(Optional.of(mockProduct));

            ProductDTO productDTO = productService.getProductById(mockProduct.getId());

            verify(productRepository, times(1)).findById(anyLong());

            assertNotNull(productDTO);
            assertEquals(productDTO.getId(), mockProduct.getId());
            assertEquals(productDTO.getName(), mockProduct.getName());
            assertEquals(productDTO.getDescription(), mockProduct.getDescription());
            assertEquals(productDTO.getPrice(), mockProduct.getPrice());
            assertEquals(productDTO.getQuantity(), mockProduct.getQuantity());
            assertEquals(productDTO.getImage(), mockProduct.getImage());
            assertEquals(productDTO.getFor_animal(), mockProduct.getForAnimal());
            assertEquals(productDTO.getUtility(), mockProduct.getUtility());
        }

        @DisplayName("failure")
        @Test
        void getProductById_failure() {
            when(productRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            int id = 1;
            ProductServiceException exception = assertThrows(ProductServiceException.class,
                    () -> productService.getProductById(id));

            verify(productRepository, times(1)).findById(anyLong());

            assertEquals(exception.getHttpStatus(), HttpStatus.NOT_FOUND);
            assertEquals(exception.getError_code(), "PRODUCT_NOT_FOUND");
            assertEquals(exception.getMessage(), "Product not found with id: " + id);
        }
    }

    @DisplayName("reduceProductQuantity")
    @Nested
    class reduceProductQuantity {

        @DisplayName("success")
        @Test
        void reduceProductQuantity_success() {
            Product mockProduct = getMockProduct();;
            int init_quantity = mockProduct.getQuantity();
            int amount = 3;

            when(productRepository.findById(anyLong()))
                    .thenReturn(Optional.of(mockProduct));

            productService.reduceProductQuantity(mockProduct.getId(), amount);

            verify(productRepository, times(1))
                    .findById(anyLong());
            verify(productRepository, times(1))
                    .save(any());

            assertEquals(mockProduct.getQuantity(), init_quantity - amount);
        }

        @DisplayName("failure 1")
        @Test
        void reduceProductQuantity_failure1() {
            int id = 1;

            when(productRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            ProductServiceException exception = assertThrows(ProductServiceException.class,
                    () -> productService.reduceProductQuantity(id, 1));

            verify(productRepository, times(1))
                    .findById(anyLong());
            verify(productRepository, times(0))
                    .save(any());

            assertEquals(exception.getMessage(), "Product not found with id: " + id);
            assertEquals(exception.getError_code(), "PRODUCT_NOT_FOUND");
            assertEquals(exception.getHttpStatus(), HttpStatus.NOT_FOUND);
        }

        @DisplayName("failure 2")
        @Test
        void reduceProductQuantity_failure2() {
            Product mockProduct = getMockProduct();
            int amount = mockProduct.getQuantity() + 1;

            when(productRepository.findById(anyLong()))
                    .thenReturn(Optional.of(mockProduct));

            ProductServiceException exception = assertThrows(ProductServiceException.class,
                    () -> productService.reduceProductQuantity(mockProduct.getId(), amount));

            verify(productRepository, times(1))
                    .findById(anyLong());
            verify(productRepository, never())
                    .save(any());

            assertEquals(exception.getMessage(), "A product can't have a negative quantity. " +
                    "Amount of " + amount + " is too high for product with id: " + mockProduct.getId());
            assertEquals(exception.getError_code(), "PRODUCT_QUANTITY_ERROR");
            assertEquals(exception.getHttpStatus(), HttpStatus.CONFLICT);
        }
    }

    @Nested
    @DisplayName("getUtilities")
    class getUtilities {

        @Test
        void getUtilities_success() {
            List<Product> productList = getMockProductList();
            when(productRepository.getDistinctUtilities())
                    .thenReturn(productList.stream().map(product -> product.getUtility())
                            .collect(Collectors.toSet())
                            .stream().toList());

            List<String> utilities = productService.getUtilities();

            verify(productRepository, times(1)).getDistinctUtilities();

            assertEquals(utilities,
                    productList.stream().map(product -> product.getUtility())
                            .collect(Collectors.toSet()).stream().toList());
        }
    }

    @Nested
    @DisplayName("getForAnimals")
    class getForAnimals {

        @Test
        void getForAnimals_success() {
            List<Product> productList = getMockProductList();
            when(productRepository.getDistinctForAnimals())
                    .thenReturn(productList.stream().map(product -> product.getForAnimal())
                            .collect(Collectors.toSet())
                            .stream().toList());

            List<String> forAnimals = productService.getForAnimals();

            verify(productRepository, times(1)).getDistinctForAnimals();

            assertEquals(forAnimals,
                    productList.stream().map(product -> product.getForAnimal())
                            .collect(Collectors.toSet()).stream().toList());
        }
    }

    private Product getMockProduct() {
        return Product.builder()
                .id(1)
                .name("name")
                .price(1.0)
                .quantity(5)
                .description("description")
                .image("image")
                .utility("utility")
                .forAnimal("for_animal")
                .build();
    }

    List<Product> getMockProductList() {
        Product product1 = Product.builder()
                .id(1)
                .name("product 1")
                .description("description 1")
                .price(1)
                .quantity(1)
                .forAnimal("for_animal 1")
                .utility("utility 1")
                .build();
        Product product2 = Product.builder()
                .id(2)
                .name("product 2")
                .description("description 2")
                .price(2)
                .quantity(2)
                .forAnimal("for_animal 2")
                .utility("utility 2")
                .build();
        return List.of(product1, product2);
    }
}