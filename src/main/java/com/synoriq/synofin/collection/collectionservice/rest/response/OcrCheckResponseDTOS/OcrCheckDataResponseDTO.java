package com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOS;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OcrCheckDataResponseDTO {

    @JsonProperty("type")
    private String type;

    @JsonProperty("result")
    public OcrCheckDetailsResponseDTO details;

}
