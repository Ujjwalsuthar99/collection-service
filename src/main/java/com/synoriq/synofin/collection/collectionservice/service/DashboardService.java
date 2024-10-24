package com.synoriq.synofin.collection.collectionservice.service;

import java.text.ParseException;

import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.rest.response.dashboarddtos.DashboardResponseDTO;

public interface DashboardService {

     DashboardResponseDTO getDashboardCountByUserId(Long userId, String userName, String startDate, String toDate) throws CollectionException, ParseException;

}
