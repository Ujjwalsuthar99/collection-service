package com.synoriq.synofin.collection.collectionservice.rest.response.systemProperties;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class ReceiptDateResponse {

    @JsonProperty("current_date")
    @JsonFormat(pattern = "yyyy/MM/dd")
    public String currentDate;

    @JsonProperty("business_date")
    @JsonFormat(pattern = "yyyy/MM/dd")
    public String businessDate;

}
