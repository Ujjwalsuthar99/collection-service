package com.synoriq.synofin.collection.collectionservice.rest.response.systemProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class GetReceiptDateResponse {

    @JsonProperty("receipt_date")
    public String receiptDate;

}
