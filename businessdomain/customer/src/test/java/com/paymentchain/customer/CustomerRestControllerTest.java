package com.paymentchain.customer;

import com.paymentchain.customer.controller.CustomerRestController;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.exception.BusinessRuleException;
import com.paymentchain.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Microservicio Customer")
class CustomerRestControllerTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private WebClient.Builder webClienteBuilder;

    @InjectMocks
    private CustomerRestController customerController;

    private Customer customerMock;

    @BeforeEach
    void setUp() {
        customerMock = new Customer();
        customerMock.setId(1L);
        customerMock.setName("Juan Perez");
        customerMock.setPhone("+56912345678");
        customerMock.setIban("CL123456789");
        customerMock.setSurname("Perez");
        customerMock.setAdress("Calle Principal 123");
    }

    @Test
    @DisplayName("list() debería retornar todos los clientes")
    void testList_RetornaClientes() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customerMock));

        ResponseEntity<List<Customer>> response = customerController.list();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Juan Perez", response.getBody().get(0).getName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("list() debería retornar 204 cuando no hay clientes")
    void testList_NoContent() {
        when(customerRepository.findAll()).thenReturn(List.of());

        ResponseEntity<List<Customer>> response = customerController.list();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("get() debería retornar 200 cuando el cliente existe")
    void testGet_Encontrado() throws BusinessRuleException {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerMock));

        ResponseEntity<?> response = customerController.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Customer body = (Customer) response.getBody();
        assertEquals("Juan Perez", body.getName());
    }

    @Test
    @DisplayName("get() debería retornar 404 cuando el cliente no existe")
    void testGet_NoEncontrado() throws BusinessRuleException {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = customerController.get(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("post() debería crear un cliente y retornar 201")
    void testPost_CreaCliente() {
        Customer input = new Customer();
        input.setName("Maria Lopez");
        input.setPhone("+56998765432");
        input.setSurname("Lopez");
        input.setProducts(List.of());

        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        ResponseEntity<?> response = customerController.post(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        Customer body = (Customer) response.getBody();
        assertEquals("Maria Lopez", body.getName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("put() debería actualizar un cliente existente")
    void testPut_ActualizaCliente() {
        Customer input = new Customer();
        input.setName("Juan Actualizado");
        input.setPhone("+56911111111");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerMock));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = customerController.put(1L, input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Customer body = (Customer) response.getBody();
        assertEquals("Juan Actualizado", body.getName());
        assertEquals("+56911111111", body.getPhone());
    }

    @Test
    @DisplayName("put() debería retornar 404 si el cliente no existe")
    void testPut_NoEncontrado() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = customerController.put(99L, new Customer());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("delete() debería eliminar un cliente")
    void testDelete_EliminaCliente() {
        doNothing().when(customerRepository).deleteById(1L);

        ResponseEntity<?> response = customerController.delete(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerRepository, times(1)).deleteById(1L);
    }
}
