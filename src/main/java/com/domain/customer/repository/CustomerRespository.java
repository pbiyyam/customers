package com.domain.customer.repository;

import com.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRespository extends JpaRepository<Customer, Long> {
    Optional<List<Customer>> findCustomerByFirstNameOrLastName(String firstName, String lastName);
    Optional<List<Customer>> findCustomerByFirstNameAndLastName(String firstName, String lastName);
}
