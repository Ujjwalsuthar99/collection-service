package com.synoriq.synofin.collection.collectionservice.rest.response.repossessiondtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RepossessionResponseDTO {

    @JsonProperty("current")
    private List<RepossessionCommonDTO> current;

    @JsonProperty("history")
    private List<RepossessionCommonDTO> history;

}

