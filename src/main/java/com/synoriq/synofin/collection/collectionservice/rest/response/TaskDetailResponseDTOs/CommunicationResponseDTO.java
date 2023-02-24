package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommunicationResponseDTO {

    @JsonProperty("numbers")
    public String numbers;

    @JsonProperty("state")
    public String state;

    @JsonProperty("address_type")
    public String addressType;

    @JsonProperty("is_address_type_primary")
    public String isAddressTypePrimary;

    @JsonProperty("full_address")
    public String fullAddress;

    @JsonProperty("pin_code")
    public String pinCode;

}