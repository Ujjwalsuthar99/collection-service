package com.synoriq.synofin.collection.collectionservice.rest.response.RepossessionDTOs;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RepossessionCommonDTO {

    @JsonProperty("status")
    private String status;

    @JsonProperty("initiated_by")
    private String initiatedBy;

    @JsonProperty("approved_by")
    private String approvedBy;

    @JsonProperty("action_by")
    private String actionBy;

    @JsonProperty("assign_to")
    private String assignTo;

    @JsonProperty("created_date")
    private Date createDate;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("attachments")
    private Object attachments;

    @JsonProperty("agency")
    private String agency;

    @JsonProperty("vehicle_handover_to")
    private String vehicleHandoverTo;

    @JsonProperty("yard_contact_number")
    private String yardContactNumber;

    @JsonProperty("yard_name")
    private String yardName;

}
