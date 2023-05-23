package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

import java.util.UUID;


@Component
public class LogInterceptor implements HandlerInterceptor {
    private static final String REQUEST_ID = "requestId";
    private static final String CLIENT_NAME = "clientName";
    private static final String COMMON = "common";
    @Autowired
    CurrentUserInfo currentUserInfo;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MDC.put(CLIENT_NAME, currentUserInfo.getClientId());
        MDC.put(COMMON,"application");
        String trackingId = request.getParameter(REQUEST_ID);
        if(StringUtils.isBlank(trackingId)) {
            trackingId = String.valueOf(UUID.randomUUID());
        }
        MDC.put(REQUEST_ID, trackingId);
        response.setHeader(REQUEST_ID, trackingId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) throws Exception {
        MDC.clear();
    }
}