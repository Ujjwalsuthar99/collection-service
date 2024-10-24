package com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.loansummaryforloandtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InstallmentAmountResponseDTO {

    @JsonProperty("paid")
    private Double paid;

    @JsonProperty("waived_amount")
    private Double waivedAmount;

    @JsonProperty("dues_as_on_date")
    private Double duesAsOnDate;

    @JsonProperty("net_amount")
    private Double netAmount;

}
