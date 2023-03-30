package com.synoriq.synofin.collection.collectionservice.rest.request.uploadImageOnS3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UploadImageOnS3DataRequestDTO {

    @JsonProperty("data")
    public UploadImageData data;

    @JsonProperty("client_id")
    public String clientId;

    @JsonProperty("system_id")
    public String systemId;
}
