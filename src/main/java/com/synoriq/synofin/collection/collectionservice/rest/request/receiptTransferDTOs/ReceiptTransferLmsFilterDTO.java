package com.synoriq.synofin.collection.collectionservice.rest.request.receiptTransferDTOs;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ReceiptTransferLmsFilterDTO {

    @JsonProperty("is_filter")
    private Boolean isFilter;

    @JsonProperty("payment_mode")
    private String paymentMode;

    @JsonProperty("from_date")
    private String fromDate;

    @JsonProperty("to_date")
    private String toDate;

    @JsonProperty("criteria")
    private List<String> criteria;

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("size")
    private Integer size;

}



