package com.synoriq.synofin.collection.collectionservice.rest.commondto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.transaction.Transactional;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Transactional
public class GeoLocationDTO {

    @JsonProperty("latitude")
    private String latitude;

    @JsonProperty("longitude")
    private String longitude;

}
