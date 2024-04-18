package com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.UploadImageResponseDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadImageOnS3DataResponse {

    @JsonProperty("downloadUrl")
    private String downloadUrl;

    @JsonProperty("fileName")
    private String fileName;

    @JsonProperty("userRefNo")
    private String userRefNo;

}
