package com.synoriq.synofin.collection.collectionservice.rest.request.emitrarequestdtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceTransactionDTO {

    @JsonProperty("revenue_head")
    private String revenueHead;

    @JsonProperty("consumer_key")
    private String consumerKey;

    @JsonProperty("consumer_name")
    private String consumerName;

    @JsonProperty("sso_id")
    private String ssoId;

    @JsonProperty("sso_token")
    private String ssoToken;

}
