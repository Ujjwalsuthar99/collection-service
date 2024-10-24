package com.synoriq.synofin.collection.collectionservice.rest.response.dynamicqrcodedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.IntegrationServiceErrorResponseDTO;
import lombok.Data;

@Data
public class DynamicQrCodeCheckStatusResponseDTO {

    @JsonProperty("response")
    private Boolean response;

    @JsonProperty("data")
    private DynamicQrCodeCheckStatusDataResponseDTO data;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("error")
    private IntegrationServiceErrorResponseDTO error;

    @JsonProperty("errorFields")
    private Object errorFields;

}
