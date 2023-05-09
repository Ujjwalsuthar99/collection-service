package com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOS;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OcrCheckResponseDTO {

    @JsonProperty("response")
    private Boolean response;

    @JsonProperty("data")
    private OcrCheckDataResponseDTO data;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("error")
    private OcrCheckErrorResponseDTO error;

    @JsonProperty("errorFields")
    private Object errorFields;

}
