package com.synoriq.synofin.collection.collectionservice.rest.response.UtilsDTOs;

import lombok.Data;

@Data
public class DashboardCountDTO {

    Double totalAmount;

    Integer totalCount;

    public DashboardCountDTO(Double totalAmount, Integer totalCount) {
        this.totalAmount = totalAmount;
        this.totalCount = totalCount;
    }
}
