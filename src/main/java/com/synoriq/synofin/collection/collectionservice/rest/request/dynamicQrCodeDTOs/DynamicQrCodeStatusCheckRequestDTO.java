package com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DynamicQrCodeStatusCheckRequestDTO {

    @JsonProperty("data")
    private DynamicQrCodeStatusCheckDataRequestDTO dynamicQrCodeStatusCheckDataRequestDTO;

}
