package com.paymentchain.product;

import com.paymentchain.product.controller.ProductRestController;
import com.paymentchain.product.entities.Product;
import com.paymentchain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Microservicio Product")
class ProductRestControllerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductRestController productController;

    private Product productMock;

    @BeforeEach
    void setUp() {
        productMock = new Product();
        productMock.setId(1L);
        productMock.setCode("PROD-001");
        productMock.setName("Extintor ABC");
    }

    @Test
    @DisplayName("list() debería retornar todos los productos")
    void testList_RetornaProductos() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(productMock));

        List<Product> result = productController.list();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Extintor ABC", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("get() debería retornar 200 cuando el producto existe")
    void testGet_Encontrado() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(productMock));

        Object result = productController.get(1L);

        assertInstanceOf(ResponseEntity.class, result);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Product body = (Product) response.getBody();
        assertEquals("PROD-001", body.getCode());
    }

    @Test
    @DisplayName("get() debería retornar 404 cuando el producto no existe")
    void testGet_NoEncontrado() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Object result = productController.get(99L);

        assertInstanceOf(ResponseEntity.class, result);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("post() debería crear un producto")
    void testPost_CreaProducto() {
        Product input = new Product();
        input.setCode("PROD-002");
        input.setName("Manguera 20m");

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        ResponseEntity<?> response = productController.post(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Product body = (Product) response.getBody();
        assertEquals("PROD-002", body.getCode());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("put() debería actualizar un producto existente")
    void testPut_ActualizaProducto() {
        Product input = new Product();
        input.setCode("PROD-001-UPD");
        input.setName("Extintor ABC Plus");

        when(productRepository.findById(1L)).thenReturn(Optional.of(productMock));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = productController.put(1L, input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Product body = (Product) response.getBody();
        assertEquals("PROD-001-UPD", body.getCode());
        assertEquals("Extintor ABC Plus", body.getName());
    }

    @Test
    @DisplayName("put() debería retornar 404 si el producto no existe")
    void testPut_NoEncontrado() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = productController.put(99L, new Product());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("delete() debería eliminar un producto")
    void testDelete_EliminaProducto() {
        doNothing().when(productRepository).deleteById(1L);

        ResponseEntity<?> response = productController.delete(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productRepository, times(1)).deleteById(1L);
    }
}
