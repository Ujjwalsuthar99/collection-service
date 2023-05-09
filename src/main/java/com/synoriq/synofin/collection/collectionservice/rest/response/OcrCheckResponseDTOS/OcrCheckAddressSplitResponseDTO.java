package com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOS;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OcrCheckAddressSplitResponseDTO {

    @JsonProperty("city")
    private String city;

    @JsonProperty("district")
    private String district;

    @JsonProperty("pin")
    private String pin;

    @JsonProperty("locality")
    private String locality;

    @JsonProperty("line2")
    private String line2;

    @JsonProperty("line1")
    private String line1;

    @JsonProperty("state")
    private String state;

    @JsonProperty("street")
    private String street;

    @JsonProperty("landmark")
    private String landmark;

    @JsonProperty("careOf")
    private String careOf;

    @JsonProperty("houseNumber")
    private String houseNumber;

    @JsonProperty("branch")
    private String branch;

}
