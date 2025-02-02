package com.synoriq.synofin.collection.collectionservice.rest.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalContactDetailsDtoRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    Date createdDate;

    Long createdBy;

    Boolean deleted;

    Long loanId;

    String contactName;

    Long mobileNumber;

    Long altMobileNumber;

    String email;

    String relationWithApplicant;

    String address;

}