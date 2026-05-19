/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/RestController.java to edit this template
 */
package com.paymentchain.customer.controller;

import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.entities.ProductResponse;
import com.paymentchain.customer.exception.BusinessRuleException;
import com.paymentchain.customer.repository.CustomerRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.reactive.function.client.WebClient;

/**
 *
 * @author SSG
 */
@RestController
@RequestMapping("/customer")
public class CustomerRestController {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    private WebClient.Builder webClienteBuilder;

    @GetMapping()
    public ResponseEntity<List<Customer>> list() {
        List<Customer> findAll = customerRepository.findAll();
        if (findAll.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(findAll);
        }
    }

    //public Object get(@PathVariable String id) {
    @GetMapping("/{id}")
    @CircuitBreaker(name = "EjemploCurso")
    public ResponseEntity<?> get(@PathVariable("id") Long id) throws BusinessRuleException {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);

        if (optionalCustomer.isPresent()) {
            Customer retorno = optionalCustomer.get();

            //WebClient webClient = WebClient.create("http://localhost:8083");
            //WebClient webClient = webClienteBuilder.create("http://BUSINESSDOMAIN-PRODUCT");
            List<ProductResponse> products = new ArrayList<>();
            List<CustomerProduct> productName = new ArrayList<>();
            try {
                for (CustomerProduct relation : retorno.getProducts()) {

                    ProductResponse product = webClienteBuilder.build()
                            .get()
                            .uri("http://BUSINESSDOMAIN-PRODUCT/product/{id}", relation.getProductId())
                            .retrieve()
                            .bodyToMono(ProductResponse.class)
                            .block();

                    /*ProductResponse product = webClient.get()
                        .uri("/product/{id}", relation.getProductId())
                        .retrieve()
                        .bodyToMono(ProductResponse.class)
                        .block();
                     */
                    if (product != null) {
                        CustomerProduct productoRespuesta = new CustomerProduct();
                        productoRespuesta.setProductName(product.getName());
                        productoRespuesta.setProductId(product.getId());
                        productName.add(productoRespuesta);
                        products.add(product);
                    }
                }
            } catch (Exception ex) {
                throw new BusinessRuleException("5020", HttpStatus.MULTI_STATUS.PRECONDITION_FAILED ,"Servicio Caído, contactse con el Admin");
            }
            retorno.setProducts(productName);

            /*ProductResponse product = webClient.get()
            .uri("/products/{id}", 1)
            .retrieve()
            .bodyToMono(ProductResponse.class)
            .block();*/
            return new ResponseEntity<>(retorno, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Customer input) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);

        if (optionalCustomer.isPresent()) {
            Customer newCustomer = optionalCustomer.get();
            newCustomer.setName(input.getName());
            newCustomer.setPhone(input.getPhone());
            Customer retorno = customerRepository.save(newCustomer);
            return new ResponseEntity<>(retorno, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Customer input) {

        input.getProducts().forEach(x -> x.setCustomer(input));
        Customer retorno = customerRepository.save(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(retorno);

        //Customer retorno=  customerRepository.save(input);
        //return ResponseEntity.ok(retorno);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        customerRepository.deleteById(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
