package com.synoriq.synofin.collection.collectionservice.rest.response.UploadImageResponseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UploadImageOnS3DataResponse {

    @JsonProperty("downloadUrl")
    private String downloadUrl;

    @JsonProperty("fileName")
    private String fileName;

}
