package com.synoriq.synofin.collection.collectionservice.rest.response.UploadImageResponseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UploadImageOnS3ResponseDTO {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public UploadImageOnS3DataResponse data;
}
