package com.synoriq.synofin.collection.collectionservice.rest.request.repossessionDTOs.lmsRepossession;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LmsRepossessionDataDTO {

    @JsonProperty("collateral_details")
    public ArrayList<LmsRepossessionCollateralDetailsDTO> collateralDetails;

    @JsonProperty("repo_date")
    public String repoDate;

    @JsonProperty("sourcing_rm")
    public String sourcingRm;

    @JsonProperty("repossession_agency")
    public String repossessionAgency;

    @JsonProperty("branch")
    public String branch;

    @JsonProperty("yard_address")
    public String yardAddress;

    @JsonProperty("charges")
    public ArrayList<Object> charges;

    @JsonProperty("remarks")
    public String remarks;

    @JsonProperty("number_of_instruments")
    public int numberOfInstruments;

    @JsonProperty("transaction_date")
    public String transactionDate;
}
