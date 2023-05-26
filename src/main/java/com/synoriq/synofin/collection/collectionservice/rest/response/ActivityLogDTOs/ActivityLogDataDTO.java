package com.synoriq.synofin.collection.collectionservice.rest.response.ActivityLogDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDataDTOs.UsersDataDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ActivityLogDataDTO {


    @JsonProperty("total_count")
    public Long totalCount;

    @JsonProperty("data")
    public List<ActivityLogCustomResponseDTO> data;

}
