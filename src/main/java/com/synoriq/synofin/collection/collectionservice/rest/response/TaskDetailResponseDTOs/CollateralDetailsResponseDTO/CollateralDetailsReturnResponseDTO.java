package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.CollateralDetailsResponseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class CollateralDetailsReturnResponseDTO {

    @JsonProperty("vehicle_type")
    public String vehicleType;

    @JsonProperty("vehicle_number")
    public String vehicleNumber;

    @JsonProperty("manufacturer")
    public String manufacturer;

    @JsonProperty("model")
    public String model;

    @JsonProperty("chasis_number")
    public String chasisNumber;

    @JsonProperty("engine_number")
    public String engineNumber;

    @JsonProperty("cost_of_asset")
    public Double costOfAsset;

}
