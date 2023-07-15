package com.synoriq.synofin.collection.collectionservice.rest.request.msgServiceRequestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsListDTO {

    @JsonProperty("mobiles")
    private String mobiles;

    @JsonProperty("message_type")
    private String messageType;

}
