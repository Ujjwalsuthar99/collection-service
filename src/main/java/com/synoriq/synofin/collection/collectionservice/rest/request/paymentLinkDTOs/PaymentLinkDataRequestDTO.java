package com.synoriq.synofin.collection.collectionservice.rest.request.paymentLinkDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class PaymentLinkDataRequestDTO implements Serializable {
    @JsonProperty("upi_link")
    public String upiLink;
    @JsonProperty("product_type")
    public String productType;
    @JsonProperty("amount")
    public int amount;
    @JsonProperty("currency")
    public String currency;
    @JsonProperty("accept_partial")
    public boolean acceptPartial;
    @JsonProperty("first_min_partial_amount")
    public int firstMinPartialAmount;
    @JsonProperty("expire_by")
    public long expireBy;
    @JsonProperty("description")
    public String description;
    @JsonProperty("customer_name")
    public String customerName;
    @JsonProperty("customer_phone_no")
    public String customerPhoneNo;
    @JsonProperty("customer_email")
    public String customerEmail;
    @JsonProperty("redirection_url")
    public String redirectionUrl;
    @JsonProperty("redirection_method")
    public String redirectionMethod;


    public PaymentLinkDataRequestDTO(String upiLink, String productType, int amount, String currency, boolean acceptPartial, int firstMinPartialAmount, long expireBy, String description, String customerName, String customerPhoneNo, String customerEmail, String redirectionUrl, String redirectionMethod) {
        this.upiLink = upiLink;
        this.productType = productType;
        this.amount = amount;
        this.currency = currency;
        this.acceptPartial = acceptPartial;
        this.firstMinPartialAmount = firstMinPartialAmount;
        this.expireBy = expireBy;
        this.description = description;
        this.customerName = customerName;
        this.customerPhoneNo = customerPhoneNo;
        this.customerEmail = customerEmail;
        this.redirectionUrl = redirectionUrl;
        this.redirectionMethod = redirectionMethod;
    }
}

