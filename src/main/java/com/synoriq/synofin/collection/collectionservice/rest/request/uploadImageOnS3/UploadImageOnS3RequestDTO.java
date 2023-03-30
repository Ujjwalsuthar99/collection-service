package com.synoriq.synofin.collection.collectionservice.rest.request.uploadImageOnS3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UploadImageOnS3RequestDTO {

    @JsonProperty("data")
    private UploadImageOnS3DataRequestDTO requestData;

    @JsonProperty("user_reference_number")
    private String userReferenceNumber;
}
