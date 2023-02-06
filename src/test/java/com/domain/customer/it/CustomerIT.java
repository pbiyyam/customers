package com.domain.customer.it;

import com.domain.customer.CustomerApplication;
import com.domain.customer.dto.AddressDto;
import com.domain.customer.dto.CustomerDto;
import com.domain.customer.dto.CustomerNameDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CustomerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerIT {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetAllCustomers() {
        List<CustomerDto> customerDtoList = this.restTemplate
                .getForObject("http://localhost:" + port + "/customers", List.class);
        assertFalse(customerDtoList.isEmpty());
        assertTrue(customerDtoList.size() > 0);
    }

    @Test
    void testSearchCustomerById() {
        CustomerDto customerDto = this.restTemplate
                .getForObject("http://localhost:" + port + "/customers/2", CustomerDto.class);
        assertNotNull(customerDto);
        assertEquals("6565SD", customerDto.getAddressDto().getPostalCode());
        assertEquals("userLastName2", customerDto.getLastName());
    }

    @Test
    void testAddCustomer() {
        Random random = new Random();
        CustomerDto customerDto = createCustomerDtoObject(createAddressDtoObject(random), random);
        CustomerDto responseEntity = this.restTemplate
                .postForObject("http://localhost:" + port + "/customers",customerDto, CustomerDto.class);
        assertNotNull(responseEntity);
        assertEquals("Customer added successfully!!!", responseEntity.getMessage());
    }

    @Test
    void testSearchCustomerByName() {
        CustomerNameDto customerNameDto = CustomerNameDto.builder()
                .firstName("userFirstName1")
                .lastName("userLastName1")
                .build();
        List<CustomerDto> response = this.restTemplate
                .postForObject("http://localhost:" + port + "/searchByName",customerNameDto, List.class);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    private static CustomerDto createCustomerDtoObject(AddressDto addressDto, Random random) {
        CustomerDto customerDto = CustomerDto.builder()
                .id(random.nextLong())
                .firstName("fname")
                .lastName("lname")
                .age(23)
                .addressDto(addressDto)
                .build();
        return customerDto;
    }

    private static AddressDto createAddressDtoObject(Random random) {
        AddressDto addressDto = AddressDto.builder()
                .addressId(random.nextLong())
                .addressLine1("addrLine1")
                .addressLine2("addrLine2")
                .postalCode("8668DF")
                .city("Utrecht")
                .country("NL")
                .build();
        return addressDto;
    }
}
