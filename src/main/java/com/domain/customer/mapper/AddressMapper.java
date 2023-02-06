package com.domain.customer.mapper;

import com.domain.customer.dto.AddressDto;
import com.domain.customer.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {
    AddressMapper MAPPER = Mappers.getMapper(AddressMapper.class);
    AddressDto mapAddressToAddressDto(Address address);
    Address mapAddressDtoToAddress(AddressDto addressDto);
}
