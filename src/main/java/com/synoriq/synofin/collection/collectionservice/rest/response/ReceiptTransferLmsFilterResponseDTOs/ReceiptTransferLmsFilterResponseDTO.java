package com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferLmsFilterResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ReceiptTransferLmsFilterResponseDTO {

    @JsonProperty("data")
    private List<Map<String, Object>> data;

    @JsonProperty("total_count")
    private int totalCount;

}
