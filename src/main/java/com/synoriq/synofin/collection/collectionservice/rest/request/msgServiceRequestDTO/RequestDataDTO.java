package com.synoriq.synofin.collection.collectionservice.rest.request.msgServiceRequestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDataDTO {

    @JsonProperty("message-type")
    private String messageType;

    @JsonProperty("sms-list")
    private List<SmsListDTO> smsList;

    @JsonProperty("template_variable")
    private List<String> templateVariable;

    @JsonProperty("template_name")
    private String templateName;

}
