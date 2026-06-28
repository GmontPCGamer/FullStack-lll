package com.paymentchain.product;

import com.paymentchain.product.entities.Product;
import com.paymentchain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Tests de Integración - Microservicio Product")
class ProductIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("CRUD completo: crear, leer, actualizar, eliminar")
    void testCrudCompleto() {
        // Create
        Product nuevo = new Product();
        nuevo.setCode("EXT-001");
        nuevo.setName("Extintor ABC");
        ResponseEntity<Product> created = restTemplate.postForEntity("/product", nuevo, Product.class);
        assertEquals(HttpStatus.OK, created.getStatusCode());
        Long id = created.getBody().getId();

        // Read
        ResponseEntity<Product> get = restTemplate.getForEntity("/product/" + id, Product.class);
        assertEquals(HttpStatus.OK, get.getStatusCode());
        assertEquals("EXT-001", get.getBody().getCode());

        // Update
        Product update = new Product();
        update.setCode("EXT-001-UPD");
        update.setName("Extintor ABC Plus");
        restTemplate.put("/product/" + id, update);
        Product updated = productRepository.findById(id).orElseThrow();
        assertEquals("EXT-001-UPD", updated.getCode());

        // List
        ResponseEntity<List> list = restTemplate.getForEntity("/product", List.class);
        assertEquals(HttpStatus.OK, list.getStatusCode());
        assertTrue(list.getBody().size() > 0);
    }

    @Test
    @DisplayName("GET /product/{id} → 404 cuando no existe")
    void testNoEncontrado() {
        ResponseEntity<Product> response = restTemplate.getForEntity("/product/9999", Product.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
