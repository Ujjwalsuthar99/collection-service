package com.synoriq.synofin.collection.collectionservice.rest.response.ocrcheckresponsedtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OcrCheckDetailsResponseDTO {

    @JsonProperty("name")
    private OcrCheckNameResponseDTO name;

    @JsonProperty("addressSplit")
    private OcrCheckAddressSplitResponseDTO addressSplit;

    @JsonProperty("phone")
    private OcrCheckPhoneResponseDTO phone;

    @JsonProperty("accNo")
    private Long accNo;

    @JsonProperty("bank")
    private String bank;

    @JsonProperty("chequeNo")
    private Long chequeNo;

    @JsonProperty("ifsc")
    private String ifsc;

    @JsonProperty("micr")
    private Long micr;

}
