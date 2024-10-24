package com.synoriq.synofin.collection.collectionservice.rest.response.receipttransferdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ReceiptTransferReceiptDataResponseDTO {

    @JsonProperty("location")
    private Object location;

    @JsonProperty("images")
    private Object images;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("name")
    private String name;

}
