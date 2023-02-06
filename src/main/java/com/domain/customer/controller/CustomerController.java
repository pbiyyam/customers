package com.domain.customer.controller;

import com.domain.customer.dto.CustomerDto;
import com.domain.customer.dto.CustomerNameDto;
import com.domain.customer.dto.CustomerPatchDto;
import com.domain.customer.service.CustomerService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    @Timed(value = "addCustomers.time", description = "Time taken to add customer")
    @PostMapping(path = "/customers",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDto> addCustomer(@Valid @RequestBody CustomerDto customerDto) {
        CustomerDto addedCustomer = customerService.addCustomer(customerDto);
        return new ResponseEntity<>(addedCustomer, HttpStatus.OK);
    }

    @Timed(value = "updateAddress.time", description = "Time taken to update customer")
    @PatchMapping(path = "/updateAddress")
    public ResponseEntity<String> updateCustomerAddress(@Valid @RequestBody CustomerPatchDto patchDto) {
        String response = customerService.updateCustomer(patchDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Timed(value = "searchCustomerById.time", description = "Time taken to search customer by id")
    @GetMapping("/customers/{id}")
    public CustomerDto searchCustomerById(@Valid @PathVariable Long id) {
        return customerService.searchCustomerById(id);
    }

    @Timed(value = "searchCustomerByName.time", description = "Time taken to search customer by name")
    @PostMapping(path = "/searchByName",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CustomerDto>> searchCustomerByName(@RequestBody CustomerNameDto customerNameDto) {
        return new ResponseEntity<>(customerService.searchCustomerByName(customerNameDto), HttpStatus.OK);
    }

    @Timed(value = "getAllCustomers.time", description = "Time taken to search all the customers present")
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<CustomerDto> customers = customerService.getCustomers();
        if (customers.isEmpty()) {
            return new ResponseEntity<>(Collections.EMPTY_LIST, HttpStatus.OK);
        }
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }
}
