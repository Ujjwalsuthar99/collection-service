package com.synoriq.synofin.collection.collectionservice.rest.response.ocrcheckresponsedtos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OcrCheckPhoneResponseDTO {

    @JsonProperty("value")
    private Long value;

    @JsonProperty("conf")
    private String conf;

}
