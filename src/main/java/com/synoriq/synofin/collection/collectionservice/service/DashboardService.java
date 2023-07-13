package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.response.DashboardDTOs.DashboardResponseDTO;

import java.util.Map;

public interface DashboardService {

    public Map<String, Map> getDashboardCountByUserId(Long userId, String userName, String startDate, String toDate) throws Exception;
    public DashboardResponseDTO temp(Long userId, String userName, String startDate, String toDate) throws Exception;

}
