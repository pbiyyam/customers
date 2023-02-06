package com.domain.customer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDto {
    private Long addressId;
    @NotBlank
    private String addressLine1;
    private String addressLine2;
    @NotBlank
    private String postalCode;
    @NotBlank
    private String city;
    @NotBlank
    private String country;
}
