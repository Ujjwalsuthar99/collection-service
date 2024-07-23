package com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs;

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

public class AllBankTransferResponseDTO {

    @JsonProperty("total_count")
    public Long totalCount;

    @JsonProperty("data")
    public List<ReceiptTransferCustomDataResponseDTO> data;

}
