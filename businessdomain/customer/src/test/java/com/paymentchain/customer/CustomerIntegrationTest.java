package com.paymentchain.customer;

import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.repository.CustomerRepository;
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
@DisplayName("Tests de Integración - Microservicio Customer")
class CustomerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /customer → crear y GET /customer → listar")
    void testCrearYListar() {
        Customer customer = new Customer();
        customer.setName("Juan Perez");
        customer.setPhone("+56912345678");
        customer.setSurname("Perez");

        ResponseEntity<Customer> created = restTemplate.postForEntity("/customer", customer, Customer.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertNotNull(created.getBody());
        assertNotNull(created.getBody().getId());

        ResponseEntity<List> list = restTemplate.getForEntity("/customer", List.class);
        assertEquals(HttpStatus.OK, list.getStatusCode());
        assertTrue(list.getBody().size() > 0);
    }

    @Test
    @DisplayName("GET /customer/{id} → 200 cuando existe")
    void testObtenerPorId() {
        Customer customer = new Customer();
        customer.setName("Maria");
        customer.setPhone("+56998765432");
        Customer saved = customerRepository.save(customer);

        ResponseEntity<Customer> response = restTemplate.getForEntity("/customer/" + saved.getId(), Customer.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Maria", response.getBody().getName());
    }

    @Test
    @DisplayName("GET /customer/{id} → 404 cuando no existe")
    void testObtenerPorIdNoExistente() {
        ResponseEntity<Customer> response = restTemplate.getForEntity("/customer/9999", Customer.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT /customer/{id} → actualizar cliente")
    void testActualizar() {
        Customer customer = new Customer();
        customer.setName("Original");
        customer.setPhone("+56900000000");
        Customer saved = customerRepository.save(customer);

        Customer update = new Customer();
        update.setName("Actualizado");
        update.setPhone("+56911111111");

        restTemplate.put("/customer/" + saved.getId(), update);

        Customer updated = customerRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Actualizado", updated.getName());
        assertEquals("+56911111111", updated.getPhone());
    }
}
