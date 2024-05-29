package com.synoriq.synofin.collection.collectionservice.rest.commondto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntegrationServiceErrorResponseDTO {

    @JsonProperty("code")
    public String code;

    @JsonProperty("message")
    public String message;

}
