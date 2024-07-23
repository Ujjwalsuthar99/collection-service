package com.synoriq.synofin.collection.collectionservice.rest.response.UtilsDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowupResponseDTO {

    @JsonProperty("followup_id")
    Long followUpId;

    @JsonProperty("loan_id")
    @NotNull(message = "Loan ID cannot be Null")
    @NotBlank(message = "Loan ID cannot be Blank")
    Long loanId;

    @JsonProperty("is_deleted")
    Boolean isDeleted;

    @JsonProperty("created_date")
    Date createdDate;

    @JsonProperty("created_by")
    Long createdBy;

    @JsonProperty("followup_reason")
    String followUpReason;

    @JsonProperty("other_followup_reason")
    String otherFollowupReason;

    @JsonProperty("next_followup_datetime")
    String nextFollowUpDateTime;

    @JsonProperty("remarks")
    String remarks;

}
