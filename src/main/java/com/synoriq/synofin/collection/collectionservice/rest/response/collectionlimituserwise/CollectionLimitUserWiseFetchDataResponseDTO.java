package com.synoriq.synofin.collection.collectionservice.rest.response.collectionlimituserwise;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Getter
public class CollectionLimitUserWiseFetchDataResponseDTO {

    @JsonProperty("cashLimit")
    Double cashLimit;

    @JsonProperty("chequeLimit")
    Double chequeLimit;

    @JsonProperty("upiLimit")
    Double upiLimit;

    @JsonProperty("rtgsLimit")
    Double rtgsLimit;

    @JsonProperty("neftLimit")
    Double neftLimit;

}
