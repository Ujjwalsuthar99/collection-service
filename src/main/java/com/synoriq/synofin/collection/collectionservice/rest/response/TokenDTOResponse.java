package com.synoriq.synofin.collection.collectionservice.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDataDTO.UsersDataDTO;
import com.synoriq.synofin.lms.commondto.rest.constants.ErrorCode;
import com.synoriq.synofin.lms.commondto.rest.response.ErrorPayLoad;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
public class TokenDTOResponse {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public String data;

    @JsonProperty("error")
    public String error;

}

