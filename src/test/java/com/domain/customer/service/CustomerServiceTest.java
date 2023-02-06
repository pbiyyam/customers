package com.domain.customer.service;

import com.domain.customer.dto.AddressDto;
import com.domain.customer.dto.CustomerDto;
import com.domain.customer.dto.CustomerNameDto;
import com.domain.customer.dto.CustomerPatchDto;
import com.domain.customer.entity.Customer;
import com.domain.customer.exception.CustomerAlreadyExistsException;
import com.domain.customer.exception.InvalidRequestException;
import com.domain.customer.exception.NoSuchCustomerExistsException;
import com.domain.customer.mapper.AddressMapper;
import com.domain.customer.mapper.CustomerMapper;
import com.domain.customer.repository.AddressRepository;
import com.domain.customer.repository.CustomerRespository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    private CustomerRespository customerRespository;
    private AddressRepository addressRepository;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerRespository = Mockito.mock(CustomerRespository.class);
        addressRepository = Mockito.mock(AddressRepository.class);
        customerService = new CustomerService(customerRespository, addressRepository);
    }
    @Test
    void testAddCustomer() {
        AddressDto addressDto = createAddressDtoObject();
        CustomerDto customerDto = createCustomerDtoObject(addressDto);
        Customer customer = CustomerMapper.MAPPER.customerDtoToCustomer(customerDto);
        when(customerRespository.findById(any())).thenReturn(null);
        when(customerRespository.save(any(Customer.class))).thenReturn(customer);
        CustomerDto response = customerService.addCustomer(customerDto);
        assertNotNull(response);
        assertEquals(response.getMessage(), "Customer added successfully!!!");
        verify(customerRespository, times(1)).findById(anyLong());
        verify(customerRespository, times(1)).save(any());
    }

    @Test
    void testAddCustomerAlreadyExists() {
        AddressDto addressDto = createAddressDtoObject();
        CustomerDto customerDto = createCustomerDtoObject(addressDto);
        when(customerRespository.findById(any())).thenReturn(Optional.ofNullable(CustomerMapper.MAPPER.customerDtoToCustomer(customerDto)));
        Exception exception = assertThrows(
                CustomerAlreadyExistsException.class,
                () -> customerService.addCustomer(customerDto));
        assertTrue(exception.getMessage().contains("already exists"));
        verify(customerRespository, times(1)).findById(anyLong());
    }

    @Test
    void testSearchCustomerByIdWithExistingCustomer() {
        AddressDto addressDto = createAddressDtoObject();
        CustomerDto customerDto = createCustomerDtoObject(addressDto);
        Customer customer = CustomerMapper.MAPPER.customerDtoToCustomer(customerDto);
        when(customerRespository.findById(any())).thenReturn(Optional.ofNullable(customer));
        CustomerDto response = customerService.searchCustomerById(anyLong());
        assertNotNull(response);
        assertEquals(response.getAge(), 23);
        assertNull(response.getMessage());
        verify(customerRespository, times(1)).findById(anyLong());
    }

    @Test
    void testSearchCustomerByIdWithNoCustomerException() {
        when(customerRespository.findById(any())).thenReturn(null);
        Exception exception = assertThrows(
                NoSuchCustomerExistsException.class,
                () -> customerService.searchCustomerById(anyLong()));
        assertTrue(exception.getMessage().contains("No Customer present"));
        verify(customerRespository, times(1)).findById(anyLong());
    }

    @Test
    void testSearchCustomerByBothNamesButEmptyResponse() {
        CustomerNameDto customerNameDto = CustomerNameDto.builder()
                .firstName("fname")
                .lastName("lname")
                .build();
        when(customerRespository.findCustomerByFirstNameAndLastName(any(), any())).thenReturn(Optional.ofNullable(List.of()));
        Exception exception = assertThrows(
                NoSuchCustomerExistsException.class,
                () -> customerService.searchCustomerByName(customerNameDto));
        assertTrue(exception.getMessage().contains("No Customer present"));
        verify(customerRespository, times(1)).findCustomerByFirstNameAndLastName(anyString(), anyString());
    }

    @Test
    void testSearchCustomerByBothNamesButNullResponse() {
        CustomerNameDto customerNameDto = CustomerNameDto.builder()
                .firstName("fname")
                .lastName("lname")
                .build();
        when(customerRespository.findCustomerByFirstNameAndLastName(any(), any())).thenReturn(Optional.ofNullable(null));
        Exception exception = assertThrows(
                NoSuchCustomerExistsException.class,
                () -> customerService.searchCustomerByName(customerNameDto));
        assertTrue(exception.getMessage().contains("No Customer present"));
        verify(customerRespository, times(1)).findCustomerByFirstNameAndLastName(anyString(), anyString());
    }

    @Test
    void testSearchCustomerByBothNamesWithSuccessResponse() {
        CustomerDto customerDto = createCustomerDtoObject(createAddressDtoObject());
        List<CustomerDto> customerDtoList = new ArrayList<>();
        customerDtoList.add(customerDto);
        List<Customer> customers = CustomerMapper.MAPPER.customerDtoListToCustomer(customerDtoList);
        CustomerNameDto customerNameDto = CustomerNameDto.builder()
                .firstName("fname")
                .lastName("lname")
                .build();
        when(customerRespository.findCustomerByFirstNameAndLastName(any(), any())).thenReturn(Optional.ofNullable(customers));
        List<CustomerDto> response = customerService.searchCustomerByName(customerNameDto);
        assertNotNull(response);
        assertEquals(response.size(), 1);
        verify(customerRespository, times(1))
                .findCustomerByFirstNameAndLastName(anyString(), anyString());
    }

    @Test
    void testSearchCustomerByFirstNameWithSuccessResponse() {
        CustomerDto customerDto = createCustomerDtoObject(createAddressDtoObject());
        List<CustomerDto> customerDtoList = new ArrayList<>();
        customerDtoList.add(customerDto);
        List<Customer> customers = CustomerMapper.MAPPER.customerDtoListToCustomer(customerDtoList);
        CustomerNameDto customerNameDto = CustomerNameDto.builder()
                .firstName("fname")
                .lastName("")
                .build();
        when(customerRespository.findCustomerByFirstNameOrLastName(any(), any())).thenReturn(Optional.ofNullable(customers));
        List<CustomerDto> response = customerService.searchCustomerByName(customerNameDto);
        assertNotNull(response);
        assertTrue(response.size()==1);
        verify(customerRespository, times(1)).
                findCustomerByFirstNameOrLastName(anyString(),anyString());
    }

    @Test
    void testSearchCustomerByFirstNameWithException() {
        CustomerNameDto customerNameDto = CustomerNameDto.builder()
                .firstName("fname")
                .lastName("")
                .build();
        when(customerRespository.findCustomerByFirstNameOrLastName(any(), any())).thenReturn(Optional.of(List.of()));
        Exception exception = assertThrows(
                NoSuchCustomerExistsException.class,
                () -> customerService.searchCustomerByName(customerNameDto));
        assertTrue(exception.getMessage().contains("No Customer present"));
        verify(customerRespository, times(1))
                .findCustomerByFirstNameOrLastName(anyString(), anyString());
    }

    @Test
    void testSearchCustomerByLastNameWithSuccessResponse() {
        CustomerDto customerDto = createCustomerDtoObject(createAddressDtoObject());
        List<CustomerDto> customerDtoList = new ArrayList<>();
        customerDtoList.add(customerDto);
        List<Customer> customers = CustomerMapper.MAPPER.customerDtoListToCustomer(customerDtoList);
        CustomerNameDto customerNameDto = CustomerNameDto.builder()
                .firstName("")
                .lastName("lname")
                .build();
        when(customerRespository.findCustomerByFirstNameOrLastName(any(), any())).thenReturn(Optional.ofNullable(customers));
        List<CustomerDto> response = customerService.searchCustomerByName(customerNameDto);
        assertNotNull(response);
        assertTrue(response.size()==1);
        verify(customerRespository, times(1)).
                findCustomerByFirstNameOrLastName(anyString(),anyString());
    }

    @Test
    void testSearchCustomerByLastNameWithException() {
        CustomerNameDto customerNameDto = CustomerNameDto.builder()
                .firstName("")
                .lastName("lname")
                .build();
        when(customerRespository.findCustomerByFirstNameOrLastName(any(), any())).thenReturn(Optional.of(List.of()));
        Exception exception = assertThrows(
                NoSuchCustomerExistsException.class,
                () -> customerService.searchCustomerByName(customerNameDto));
        assertTrue(exception.getMessage().contains("No Customer present"));
        verify(customerRespository, times(1))
                .findCustomerByFirstNameOrLastName(anyString(), anyString());
    }

    @Test
    void testSearchCustomerByLastNameWithExceptionOne() {
        CustomerNameDto customerNameDto = CustomerNameDto.builder()
                .firstName("")
                .lastName("lname")
                .build();
        when(customerRespository.findCustomerByFirstNameOrLastName(any(), any())).thenReturn(Optional.ofNullable(null));
        Exception exception = assertThrows(
                NoSuchCustomerExistsException.class,
                () -> customerService.searchCustomerByName(customerNameDto));
        assertTrue(exception.getMessage().contains("or last name"));
        verify(customerRespository, times(1))
                .findCustomerByFirstNameOrLastName(anyString(), anyString());
    }

    @Test
    public void testSearchCustomerByNameWithNoNames() {
        CustomerNameDto customerNameDto = CustomerNameDto.builder()
                .firstName("")
                .lastName("")
                .build();
        Exception exception = assertThrows(
                InvalidRequestException.class,
                () -> customerService.searchCustomerByName(customerNameDto));
        assertTrue(exception.getMessage().contains("Invalid input"));
    }

    @Test
    void testUpdateCustomerWithSuccessResponse() {
        CustomerDto customerDto = CustomerDto.builder()
                .id(3l)
                .addressDto(AddressDto.builder().addressId(1l).build())
                .build();
        Customer customer = CustomerMapper.MAPPER.customerDtoToCustomer(customerDto);
        CustomerPatchDto customerPatchDto = CustomerPatchDto.builder()
                .customerId(3l)
                .addressDto(createAddressDtoObject())
                .build();
        when(customerRespository.findById(anyLong())).thenReturn(Optional.ofNullable(customer));
        when(addressRepository.save(any())).thenReturn(AddressMapper.MAPPER.mapAddressDtoToAddress(createAddressDtoObject()));
        String response = customerService.updateCustomer(customerPatchDto);
        assertNotNull(response);
        assertTrue(response.contains("updated successfully"));
        verify(customerRespository, times(1)).findById(anyLong());
        verify(addressRepository, times(1)).save(any());
    }

    @Test
    void testUpdateCustomerWithFailureResponse() {
        CustomerDto customerDto = CustomerDto.builder()
                .id(3l)
                .addressDto(AddressDto.builder().addressId(1l).build())
                .build();
        Customer customer = CustomerMapper.MAPPER.customerDtoToCustomer(customerDto);
        CustomerPatchDto customerPatchDto = CustomerPatchDto.builder()
                .customerId(3l)
                .addressDto(createAddressDtoObject())
                .build();
        when(customerRespository.findById(anyLong())).thenReturn(Optional.ofNullable(customer));
        when(addressRepository.save(any())).thenReturn(null);
        String response = customerService.updateCustomer(customerPatchDto);
        assertNotNull(response);
        assertTrue(response.contains("update failed"));
        verify(customerRespository, times(1)).findById(anyLong());
        verify(addressRepository, times(1)).save(any());
    }

    @Test
    void testGetAllCustomersWithSuccess() {
        CustomerDto customerDto = createCustomerDtoObject(createAddressDtoObject());
        List<CustomerDto> customerDtoList = new ArrayList<>();
        customerDtoList.add(customerDto);
        List<Customer> customerList = CustomerMapper.MAPPER.customerDtoListToCustomer(customerDtoList);
        when(customerRespository.findAll()).thenReturn(customerList);
        List<CustomerDto> response = customerService.getCustomers();
        assertNotNull(response);
        verify(customerRespository, times(1)).findAll();
    }

    @Test
    void testGetAllCustomersWithFailure() {
        when(customerRespository.findAll()).thenReturn(List.of());
        List<CustomerDto> response = customerService.getCustomers();
        assertNotNull(response);
        assertEquals(response.get(0).getMessage(), "No customers found, please add customers");
        verify(customerRespository, times(1)).findAll();
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
