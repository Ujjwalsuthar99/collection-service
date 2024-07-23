package com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DisableApproveButtonResponseDTO {

    @JsonProperty("disableApproveButton")
    private Boolean disableApproveButton;

}
