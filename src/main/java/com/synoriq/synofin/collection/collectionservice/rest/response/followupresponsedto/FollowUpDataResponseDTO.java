package com.synoriq.synofin.collection.collectionservice.rest.response.followupresponsedto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class FollowUpDataResponseDTO {

    @JsonProperty("total_count")
    public Long totalCount;

    @JsonProperty("data")
    public List<FollowUpCustomDataResponseDTO> data;

}
