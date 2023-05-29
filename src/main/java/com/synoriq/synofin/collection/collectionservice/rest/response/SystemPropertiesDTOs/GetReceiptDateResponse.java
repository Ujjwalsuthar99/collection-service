package com.synoriq.synofin.collection.collectionservice.rest.response.SystemPropertiesDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class GetReceiptDateResponse {

    @JsonProperty("business_date")
    public String businessDate;

    @JsonProperty("transaction_date")
    public String transactionDate;

}
