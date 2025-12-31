package com.islamhamada.petshop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.islamhamada.petshop.contracts.dto.ProductDTO;
import com.islamhamada.petshop.contracts.model.RestExceptionResponse;
import com.islamhamada.petshop.entity.Product;
import com.islamhamada.petshop.model.ProductRequest;
import com.islamhamada.petshop.model.ReduceQuantityRequest;
import com.islamhamada.petshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@EnableConfigurationProperties
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper
            = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    SimpleGrantedAuthority adminRole = new SimpleGrantedAuthority("ROLE_Admin");
    SimpleGrantedAuthority customerRole = new SimpleGrantedAuthority("ROLE_Customer");

    @BeforeEach
    public void setup() {
        productRepository.deleteAll();
    }

    @Nested
    public class getAllProducts {

        @Test
        public void success() throws Exception {
            Product product = getMockProduct();
            Product product2 = getMockProduct2();
            productRepository.save(product);
            productRepository.save(product2);

            MvcResult mvcResult = mockMvc.perform(get("/product"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String response = mvcResult.getResponse().getContentAsString();
            List<ProductDTO> products = objectMapper.readValue(response, new TypeReference<List<ProductDTO>>(){});
            assertEquals(2, products.size());

            ProductDTO p1 = products.get(0);
            ProductDTO p2 = products.get(1);

            assertEquals(product.getId(), p1.getId());
            assertEquals(product.getName(), p1.getName());
            assertEquals(product.getPrice(), p1.getPrice());
            assertEquals(product.getUtility(), p1.getUtility());
            assertEquals(product.getForAnimal(), p1.getFor_animal());
            assertEquals(product.getImage(), p1.getImage());
            assertEquals(product.getQuantity(), p1.getQuantity());
            assertEquals(product.getDescription(), p1.getDescription());

            assertEquals(product2.getId(), p2.getId());
            assertEquals(product2.getName(), p2.getName());
            assertEquals(product2.getPrice(), p2.getPrice());
            assertEquals(product2.getUtility(), p2.getUtility());
            assertEquals(product2.getForAnimal(), p2.getFor_animal());
            assertEquals(product2.getImage(), p2.getImage());
            assertEquals(product2.getQuantity(), p2.getQuantity());
            assertEquals(product2.getDescription(), p2.getDescription());
        }
    }

    @Nested
    public class createProduct {
        SimpleGrantedAuthority neededRole = adminRole;
        SimpleGrantedAuthority notNeededRole = customerRole;

        @Test
        public void success() throws Exception {
            ProductRequest request = new ProductRequest("name");
            MvcResult mvcResult = mockMvc.perform(post("/product")
                            .with(jwt().authorities(neededRole))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request))
                    ).andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            ProductDTO product = objectMapper.readValue(response, ProductDTO.class);
            assertEquals(request.getName(), product.getName());
        }

        @Test
        public void failure_duplicate() throws Exception {
            Product product = getMockProduct();
            productRepository.save(product);
            ProductRequest request = new ProductRequest(product.getName());
            MvcResult mvcResult = mockMvc.perform(post("/product")
                            .with(jwt().authorities(neededRole))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request))
                    ).andExpect(MockMvcResultMatchers.status().isConflict())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            RestExceptionResponse exceptionResponse = objectMapper.readValue(response, RestExceptionResponse.class);
            assertEquals("PRODUCT_NAME_ALREADY_TAKEN", exceptionResponse.getError_code());
            assertEquals("A product already exists with name: " + request.getName(), exceptionResponse.getError_message());
        }

        @Test
        public void failure_no_permission() throws Exception {
            ProductRequest request = new ProductRequest("name");
            mockMvc.perform(post("/product")
                    .with(jwt().authorities(notNeededRole))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andReturn();
        }

        @ParameterizedTest
        @MethodSource("bad_input")
        public void failure_bad_input(ProductRequest request) throws Exception {
            mockMvc.perform(post("/product")
                            .with(jwt().authorities(neededRole))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request))
                    ).andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();
        }

        public static List<ProductRequest> bad_input() {
            List<ProductRequest> list = new ArrayList<>();
            list.add(new ProductRequest(""));
            list.add(new ProductRequest(null));
            return list;
        }
    }

    @Nested
    public class getProductById {

        @Test
        public void success() throws Exception {
            Product product = getMockProduct();
            productRepository.save(product);
            MvcResult mvcResult = mockMvc.perform(get("/product/" + product.getId()))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            ProductDTO productDTO = objectMapper.readValue(response, ProductDTO.class);
            assertEquals(product.getId(), productDTO.getId());
            assertEquals(product.getName(), productDTO.getName());
            assertEquals(product.getPrice(), productDTO.getPrice());
            assertEquals(product.getImage(), productDTO.getImage());
            assertEquals(product.getDescription(), productDTO.getDescription());
            assertEquals(product.getForAnimal(), productDTO.getFor_animal());
            assertEquals(product.getUtility(), productDTO.getUtility());
            assertEquals(product.getQuantity(), productDTO.getQuantity());
        }

        @Test
        public void failure_bad_input() throws Exception {
            long product_id = -1;
            mockMvc.perform(get("/product/" + product_id))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();
        }

        @Test
        public void failure_no_product() throws Exception {
            long product_id = 1;
            MvcResult mvcResult = mockMvc.perform(get("/product/" + product_id))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            RestExceptionResponse exceptionResponse = objectMapper.readValue(response, RestExceptionResponse.class);
            assertEquals("PRODUCT_NOT_FOUND", exceptionResponse.getError_code());
            assertEquals("Product not found with id: " + product_id, exceptionResponse.getError_message());
        }
    }

    @Nested
    public class reduceProductQuantity {

        SimpleGrantedAuthority neededRole = customerRole;
        SimpleGrantedAuthority notNeededRole = adminRole;

        @Test
        public void success() throws Exception {
            Product product = getMockProduct();
            productRepository.save(product);
            ReduceQuantityRequest request = getMockReduceQuantityRequest();
            MvcResult mvcResult = mockMvc.perform(put("/product/" + product.getId())
                    .with(jwt().authorities(neededRole))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            assertEquals(product.getQuantity() - request.getAmount(),
                    productRepository.findById(product.getId()).get().getQuantity());
        }

        @Test
        public void failure_no_product() throws Exception {
            long product_id = 1;
            ReduceQuantityRequest request = getMockReduceQuantityRequest();
            MvcResult mvcResult = mockMvc.perform(put("/product/" + product_id)
                    .with(jwt().authorities(neededRole))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            RestExceptionResponse exceptionResponse = objectMapper.readValue(response, RestExceptionResponse.class);
            assertEquals("PRODUCT_NOT_FOUND", exceptionResponse.getError_code());
            assertEquals("Product not found with id: " + product_id, exceptionResponse.getError_message());
        }

        @Test
        public void failure_quantity_too_high() throws Exception {
            Product product = getMockProduct();
            productRepository.save(product);
            ReduceQuantityRequest request = new ReduceQuantityRequest(product.getQuantity() + 1);
            MvcResult result = mockMvc.perform(put("/product/" + product.getId())
                    .with(jwt().authorities(neededRole))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(MockMvcResultMatchers.status().isConflict())
                    .andReturn();
            String response = result.getResponse().getContentAsString();
            RestExceptionResponse exceptionResponse = objectMapper.readValue(response, RestExceptionResponse.class);
            assertEquals("PRODUCT_QUANTITY_ERROR", exceptionResponse.getError_code());
            assertEquals("A product can't have a negative quantity. Amount of " + request.getAmount() + " is too high for product with id: " + product.getId(),
                    exceptionResponse.getError_message());
        }

        @Test
        public void failure_no_permission() throws Exception {
            ReduceQuantityRequest request = getMockReduceQuantityRequest();
            mockMvc.perform(put("/product/1")
                    .with(jwt().authorities(notNeededRole))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andReturn();
        }


        @ParameterizedTest
        @MethodSource("bad_input")
        public void failure_bad_input(long product_id, ReduceQuantityRequest request) throws Exception {
            mockMvc.perform(put("/product/" + product_id)
                    .with(jwt().authorities(neededRole))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();
        }

        public static List<Arguments> bad_input() {
            List<Arguments> list = new ArrayList<Arguments>();
            list.add(Arguments.of(-1, new ReduceQuantityRequest(1)));
            list.add(Arguments.of(1, new ReduceQuantityRequest(0)));
            list.add(Arguments.of(1, new ReduceQuantityRequest(-1)));
            return list;
        }

        private ReduceQuantityRequest getMockReduceQuantityRequest() {
            return new ReduceQuantityRequest(5);

        }
    }

    @Nested
    public class getUtilities {

        @Test
        public void success() throws Exception {
            Product product = getMockProduct();
            Product product2 = getMockProduct2();
            productRepository.save(product);
            productRepository.save(product2);

            MvcResult mvcResult = mockMvc.perform(get("/product/utilities"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String response = mvcResult.getResponse().getContentAsString();
            List<String> utilities = objectMapper.readValue(response, new TypeReference<List<String>>() {});

            assertEquals(2, utilities.size());
            assertEquals(product.getUtility(), utilities.get(0));
            assertEquals(product2.getUtility(), utilities.get(1));
        }
    }

    @Nested
    public class getForAnimals {

        @Test
        public void success() throws Exception {
            Product product = getMockProduct();
            Product product2 = getMockProduct2();
            productRepository.save(product);
            productRepository.save(product2);

            MvcResult mvcResult = mockMvc.perform(get("/product/for_animals"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();

            String response = mvcResult.getResponse().getContentAsString();
            List<String> for_animals = objectMapper.readValue(response, new TypeReference<List<String>>() {});

            assertEquals(2, for_animals.size());
            assertEquals(product.getForAnimal(), for_animals.get(0));
            assertEquals(product2.getForAnimal(), for_animals.get(1));
        }
    }

    public Product getMockProduct() {
        Product product = Product.builder()
                .name("name")
                .price(100)
                .description("description")
                .forAnimal("animal")
                .quantity(10)
                .utility("utility")
                .image("image")
                .build();
        return product;
    }

    public Product getMockProduct2() {
        Product product = Product.builder()
                .name("name2")
                .price(200)
                .description("description2")
                .forAnimal("animal2")
                .quantity(20)
                .utility("utility2")
                .image("image2")
                .build();
        return product;
    }
}