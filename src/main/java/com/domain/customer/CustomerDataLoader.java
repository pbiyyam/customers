package com.domain.customer;

import com.domain.customer.dto.AddressDto;
import com.domain.customer.dto.CustomerDto;
import com.domain.customer.mapper.CustomerMapper;
import com.domain.customer.repository.CustomerRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerDataLoader implements CommandLineRunner {
    private final CustomerRespository customerRespository;

    @Override
    public void run(String... strings) {
        CustomerDto customerDto = createCustomerDtoObject("userFirstName1","userLastName1",
                23, "addressline1","addressline2", "8888JK", "Amsterdam", "NL", "");
        CustomerDto customerDto1 = createCustomerDtoObject("userFirstName2","userLastName2",
                 31,"addressline3", "addressline4", "6565SD", "Utrecht", "NL", null);
        this.customerRespository.save(CustomerMapper.MAPPER.customerDtoToCustomer(customerDto));
        this.customerRespository.save(CustomerMapper.MAPPER.customerDtoToCustomer(customerDto1));
    }

    private CustomerDto createCustomerDtoObject(String firstName,
                                                String lastName, Integer age,
                                                String addressLine1, String addressLine2,
                                                String postalCode, String city,
                                                String country,
                                                String msg) {
        CustomerDto accountDto = CustomerDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .age(age)
                .addressDto(createAddressDtoObject(addressLine1, addressLine2,
                        postalCode, city, country))
                .message(msg)
                .build();
        return accountDto;
    }

    private AddressDto createAddressDtoObject(String addressLine1, String addressLine2,
                                              String postalCode, String city,
                                              String country) {
        AddressDto addressDto = AddressDto.builder()
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .postalCode(postalCode)
                .city(city)
                .country(country)
                .build();
        return addressDto;
    }
}
