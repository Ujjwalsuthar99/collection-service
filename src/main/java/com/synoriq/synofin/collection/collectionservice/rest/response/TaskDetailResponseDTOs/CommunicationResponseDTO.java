package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommunicationResponseDTO {

    @JsonProperty("primary_number")
    public String primaryNumber;

    @JsonProperty("secondary_number")
    public String secondaryNumber;

    @JsonProperty("whatsapp_number")
    public String whatsappNumber;

    @JsonProperty("state")
    public String state;

    @JsonProperty("city")
    public String city;

    @JsonProperty("email_id")
    public String emailId;

    @JsonProperty("address_type")
    public String addressType;

    @JsonProperty("is_address_type_primary")
    public String isAddressTypePrimary;

    @JsonProperty("full_address")
    public String fullAddress;

    @JsonProperty("pin_code")
    public String pinCode;

}