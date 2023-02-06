package com.domain.customer.controller;

import com.domain.customer.dto.AddressDto;
import com.domain.customer.dto.CustomerDto;
import com.domain.customer.dto.CustomerNameDto;
import com.domain.customer.dto.CustomerPatchDto;
import com.domain.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.aop.TimedAspect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = CustomerController.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    TimedAspect timedAspect;
    @MockBean
    CustomerService customerService;

    @Test
    void testSearchCustomerById() throws Exception {
        CustomerDto customerDto = createCustomerDtoObject(createAddressDtoObject());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                        "/customers/{id}", "1")
                .accept(MediaType.APPLICATION_JSON);
        Mockito.when(customerService.searchCustomerById(anyLong())).thenReturn(customerDto);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result);
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    void testSearchCustomerByName() throws Exception {
        CustomerDto customerDto = createCustomerDtoObject(createAddressDtoObject());
        CustomerNameDto customerNameDto = CustomerNameDto.builder()
                .firstName("fname")
                .lastName("lname")
                .build();
        List<CustomerDto> customerDtoList = new ArrayList<>();
        customerDtoList.add(customerDto);
        Mockito.when(customerService.searchCustomerByName(any(CustomerNameDto.class))).thenReturn(customerDtoList);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/searchByName")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerNameDto))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getContentAsString());
        verify(customerService, times(1)).searchCustomerByName(any());
    }

    @Test
    void testGetAllCustomers() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                        "/customers")
                .accept(MediaType.APPLICATION_JSON);
        Mockito.when(customerService.getCustomers()).thenReturn(Collections.emptyList());
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result);
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(result.getResponse().getContentAsString());
        assertEquals("[]", result.getResponse().getContentAsString());
    }

    @Test
    void testAddCustomer() throws Exception {
        CustomerDto customerDto = createCustomerDtoObject(createAddressDtoObject());
        Mockito.when(customerService.addCustomer(any())).thenReturn(customerDto);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDto))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void testUpdateCustomerAddress() throws Exception {
        CustomerPatchDto customerPatchDto = CustomerPatchDto.builder()
                .customerId(2l)
                .addressDto(createAddressDtoObject())
                .build();
        Mockito.when(customerService.updateCustomer(any())).thenReturn("Updated Successfully");
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch("/updateAddress")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerPatchDto))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    private static CustomerDto createCustomerDtoObject(AddressDto addressDto) {
        CustomerDto customerDto = CustomerDto.builder()
                .id(1l)
                .firstName("fname")
                .lastName("lname")
                .age(23)
                .addressDto(addressDto)
                .build();
        return customerDto;
    }

    private static AddressDto createAddressDtoObject() {
        AddressDto addressDto = AddressDto.builder()
                .addressId(1l)
                .addressLine1("addrLine1")
                .addressLine2("addrLine2")
                .postalCode("8668DF")
                .city("Utrecht")
                .country("NL")
                .build();
        return addressDto;
    }
}
