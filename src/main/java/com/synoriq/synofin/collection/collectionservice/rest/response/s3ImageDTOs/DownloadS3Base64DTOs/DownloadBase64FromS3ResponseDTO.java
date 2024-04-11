package com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.DownloadS3Base64DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOs.IntegrationServiceErrorResponseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DownloadBase64FromS3ResponseDTO {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public String data;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("error")
    private IntegrationServiceErrorResponseDTO error;

    @JsonProperty("errorFields")
    private Object errorFields;

}
