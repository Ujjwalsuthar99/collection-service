package com.synoriq.synofin.collection.collectionservice.rest.request.repossessionDTOs.lmsRepossession;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LmsRepossessionCollateralDetailsDTO {

    @JsonProperty("collateral_id")
    public Long collateralId;

    @JsonProperty("collateral_type")
    public String collateralType;
}
