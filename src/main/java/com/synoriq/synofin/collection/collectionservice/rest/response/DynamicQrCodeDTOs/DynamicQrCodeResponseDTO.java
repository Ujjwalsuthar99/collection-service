package com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeDataRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOs.IntegrationServiceErrorResponseDTO;
import lombok.Data;

@Data
public class DynamicQrCodeResponseDTO {

    @JsonProperty("response")
    private boolean response;

    @JsonProperty("data")
    private DynamicQrCodeDataResponseDTO data;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("error")
    private IntegrationServiceErrorResponseDTO error;

    @JsonProperty("errorFields")
    private Object errorFields;

}
