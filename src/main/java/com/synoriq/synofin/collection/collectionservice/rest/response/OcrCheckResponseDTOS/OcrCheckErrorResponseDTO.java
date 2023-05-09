package com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOS;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OcrCheckErrorResponseDTO {

    @JsonProperty("code")
    public String code;

    @JsonProperty("text")
    public String text;

}
