package com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReceiptTransferDTOResponse {
    @JsonProperty("receiver")
    public List<Map<String, Object>> receiver;

    @JsonProperty("transfer")
    public List<Map<String, Object>> transfer;

}

