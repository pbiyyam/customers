package com.domain.customer.service;

import com.domain.customer.dto.CustomerDto;
import com.domain.customer.dto.CustomerNameDto;
import com.domain.customer.dto.CustomerPatchDto;
import com.domain.customer.entity.Address;
import com.domain.customer.entity.Customer;
import com.domain.customer.exception.CustomerAlreadyExistsException;
import com.domain.customer.exception.InvalidRequestException;
import com.domain.customer.exception.NoSuchCustomerExistsException;
import com.domain.customer.mapper.AddressMapper;
import com.domain.customer.mapper.CustomerMapper;
import com.domain.customer.repository.AddressRepository;
import com.domain.customer.repository.CustomerRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRespository customerRespository;
    private final AddressRepository addressRepository;

    public CustomerDto addCustomer(CustomerDto customerDto) {
        Optional<Customer> existingCustomer = customerRespository.findById(customerDto.getId());
        if (existingCustomer == null || !existingCustomer.isPresent()) {
            Customer customerEntity = CustomerMapper.MAPPER.customerDtoToCustomer(customerDto);
            CustomerDto addedCustomer = CustomerMapper.MAPPER.customerToCustomerDto(customerRespository.save(customerEntity));
            addedCustomer.setMessage("Customer added successfully!!!");
            return addedCustomer;
        } else {
            log.info("If customer already exists, exception is thrown");
            throw new CustomerAlreadyExistsException("Customer with this id already exists!!!");
        }
    }

    public CustomerDto searchCustomerById(Long id) {
        Optional<Customer> customer = customerRespository.findById(id);
        if (null == customer || !customer.isPresent()) {
            throw new NoSuchCustomerExistsException("No Customer present with Id " + id);
        } else {
            return CustomerMapper.MAPPER.customerToCustomerDto(customer.get());
        }
    }

    public List<CustomerDto> searchCustomerByName(CustomerNameDto customerNameDto) {
        List<Customer> customerList;
        validateNotNullAndThrowException(customerNameDto);
        customerList = getCustomerListByName(customerNameDto);
        if (!customerList.isEmpty()) {
            return CustomerMapper.MAPPER.customerToCustomerDtoList(customerList);
        } else {
            throw new NoSuchCustomerExistsException("No Customer present with first name " +
                    customerNameDto.getFirstName() + " and last name " + customerNameDto.getLastName());
        }
    }

    private List<Customer> getCustomerListByName(CustomerNameDto customerNameDto) {
        if (StringUtils.isNotBlank(customerNameDto.getFirstName()) &&
                StringUtils.isNotBlank(customerNameDto.getLastName())) {
            return customerRespository
                    .findCustomerByFirstNameAndLastName(customerNameDto.getFirstName(), customerNameDto.getLastName())
                    .orElseThrow(() ->
                            new NoSuchCustomerExistsException("No Customer present with first name " +
                                    customerNameDto.getFirstName() + " and last name " + customerNameDto.getLastName()));
        } else {
            return customerRespository.findCustomerByFirstNameOrLastName(customerNameDto.getFirstName(), customerNameDto.getLastName()).
                    orElseThrow(() ->
                            new NoSuchCustomerExistsException("No Customer present with first name "
                                    + customerNameDto.getFirstName() + "or last name "+customerNameDto.getLastName()));
        }
    }

    private static void validateNotNullAndThrowException(CustomerNameDto customerNameDto) {
        if (StringUtils.isBlank(customerNameDto.getFirstName()) &&
                StringUtils.isBlank(customerNameDto.getLastName())) {
            log.error("Invalid input {}", customerNameDto);
            throw new InvalidRequestException("Invalid input, please try with valid data!!!");
        }
    }

    public List<CustomerDto> getCustomers() {
        List<Customer> customerList = customerRespository.findAll();
        if (customerList.isEmpty()) {
            return prepareEmptyCustomerDtoList();
        }
        return CustomerMapper.MAPPER.customerToCustomerDtoList(customerList);
    }

    private static List<CustomerDto> prepareEmptyCustomerDtoList() {
        CustomerDto customerDto = CustomerDto.builder()
                .message("No customers found, please add customers")
                .build();
        List<CustomerDto> customerDtoList = new ArrayList<>();
        customerDtoList.add(customerDto);
        return customerDtoList;
    }

    public String updateCustomer(CustomerPatchDto patchDto) {
        Customer existingCustomer = customerRespository.findById(patchDto.getCustomerId())
                .orElseThrow(() -> new NoSuchCustomerExistsException("No Customer present with Id " + patchDto.getCustomerId()));
        Address updateCurrentAddress = AddressMapper.MAPPER.mapAddressDtoToAddress(patchDto.getAddressDto());
        updateCurrentAddress.setAddressId(existingCustomer.getAddress().getAddressId());
        updateCurrentAddress.setCreationTime(existingCustomer.getAddress().getCreationTime());
        Address address = addressRepository.save(updateCurrentAddress);
        if (null != address) {
            return "Customer details updated successfully with id "+patchDto.getCustomerId();
        }
        return "Customer details update failed for the id "+patchDto.getCustomerId();
    }
}
