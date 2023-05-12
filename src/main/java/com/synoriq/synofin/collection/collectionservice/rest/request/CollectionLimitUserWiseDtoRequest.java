package com.synoriq.synofin.collection.collectionservice.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CollectionLimitUserWiseDtoRequest {

    @JsonProperty("collectionLimitStrategiesKey")
    private String collectionLimitStrategiesKey;

    @JsonProperty("username")
    private String username;

    @JsonProperty("totalLimitValue")
    private Double totalLimitValue;

    @JsonProperty("utilizedLimitValue")
    private Double utilizedLimitValue;
}
