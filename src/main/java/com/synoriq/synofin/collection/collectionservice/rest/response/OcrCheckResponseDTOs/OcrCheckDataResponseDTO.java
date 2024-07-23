package com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OcrCheckDataResponseDTO {

    @JsonProperty("type")
    private String type;

    @JsonProperty("details")
    public OcrCheckDetailsResponseDTO details;

}
