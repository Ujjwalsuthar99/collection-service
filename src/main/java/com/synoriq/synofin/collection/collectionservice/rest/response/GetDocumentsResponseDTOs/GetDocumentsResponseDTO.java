package com.synoriq.synofin.collection.collectionservice.rest.response.GetDocumentsResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.response.ShortenUrlDTOs.ShortenUrlDataResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class GetDocumentsResponseDTO {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public List<GetDocumentsDataResponseDTO> data;

    @JsonProperty("error")
    public Object error;
}
