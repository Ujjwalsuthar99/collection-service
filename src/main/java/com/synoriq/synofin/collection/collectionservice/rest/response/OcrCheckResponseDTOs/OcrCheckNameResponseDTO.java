package com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OcrCheckNameResponseDTO {

    @JsonProperty("value")
    private List<String> value;

    @JsonProperty("conf")
    private String conf;

}
