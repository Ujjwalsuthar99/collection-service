package com.synoriq.synofin.collection.collectionservice.rest.response.CollectionLimitUserWise;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class CollectionLimitUserWiseFetchDataResponseDTO {

    @JsonProperty("cashLimit")
    Double cashLimit;

    @JsonProperty("chequeLimit")
    Double chequeLimit;

    @JsonProperty("upiLimit")
    Double upiLimit;

}
