package com.synoriq.synofin.collection.collectionservice.rest.response.activitylogdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ActivityLogDataDTO {


    @JsonProperty("total_count")
    public Long totalCount;

    @JsonProperty("data")
    public List<ActivityLogCustomResponseDTO> data;

}
