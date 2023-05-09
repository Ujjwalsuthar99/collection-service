package com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOS;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OcrCheckDetailsResponseDTO {

    @JsonProperty("name")
    private OcrCheckNameResponseDTO name;

    @JsonProperty("addressSplit")
    private OcrCheckAddressSplitResponseDTO addressSplit;

    @JsonProperty("phone")
    private OcrCheckPhoneResponseDTO phone;

    @JsonProperty("accNo")
    private String accNo;

    @JsonProperty("bank")
    private String bank;

    @JsonProperty("chequeNo")
    private String chequeNo;

    @JsonProperty("ifsc")
    private String ifsc;

    @JsonProperty("micr")
    private String micr;

}
