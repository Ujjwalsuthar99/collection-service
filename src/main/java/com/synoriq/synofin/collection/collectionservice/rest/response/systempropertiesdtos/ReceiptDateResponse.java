package com.synoriq.synofin.collection.collectionservice.rest.response.systempropertiesdtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ReceiptDateResponse {

    @JsonProperty("current_date")
    @JsonFormat(pattern = "yyyy/MM/dd")
    public String currentDate;

    @JsonProperty("business_date")
    @JsonFormat(pattern = "yyyy/MM/dd")
    public String businessDate;

}
