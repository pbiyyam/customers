package com.domain.customer.mapper;

import com.domain.customer.dto.CustomerDto;
import com.domain.customer.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {AddressMapper.class},
unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {
    CustomerMapper MAPPER = Mappers.getMapper(CustomerMapper.class);
    @Mappings({
            @Mapping(target = "addressDto", source = "address")
    })
    CustomerDto customerToCustomerDto(Customer customer);
    @Mappings({
            @Mapping(target = "address", source = "addressDto")
    })
    Customer customerDtoToCustomer(CustomerDto customerDto);
    @Mappings({
            @Mapping(target = "addressDto", source = "address")
    })
    List<CustomerDto> customerToCustomerDtoList(List<Customer> customerList);
    List<Customer> customerDtoListToCustomer(List<CustomerDto> customerDtoList);
}
