package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.response.DashboardDTOs.DashboardResponseDTO;

import java.util.Map;

public interface DashboardService {

    public DashboardResponseDTO getDashboardCountByUserId(Long userId, String userName, String startDate, String toDate) throws Exception;

}
