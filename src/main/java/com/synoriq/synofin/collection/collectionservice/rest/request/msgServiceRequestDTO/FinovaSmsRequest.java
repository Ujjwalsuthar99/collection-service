package com.synoriq.synofin.collection.collectionservice.rest.request.msgServiceRequestDTO;

import lombok.Data;

@Data
public class FinovaSmsRequest {

    String flow_id;
    String sender;
    String short_url;
    String mobiles;
    String amount;
    String loanNumber;
    String url;
}
