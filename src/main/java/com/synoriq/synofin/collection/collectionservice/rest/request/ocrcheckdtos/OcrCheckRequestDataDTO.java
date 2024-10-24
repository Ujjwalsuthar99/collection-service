package com.synoriq.synofin.collection.collectionservice.rest.request.ocrcheckdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OcrCheckRequestDataDTO {

    @JsonProperty("img_base_url")
    private String imgBaseUrl;

    @JsonProperty("img_type")
    private String imgType;

}
