package com.synoriq.synofin.collection.collectionservice.service;

import java.util.Map;

public interface DashboardService {

    public Map<String, Map> getDashboardCountByUserId(Long userId, String userName, String startDate, String toDate) throws Exception;

}
