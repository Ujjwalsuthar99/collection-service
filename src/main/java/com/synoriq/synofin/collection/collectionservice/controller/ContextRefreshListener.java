package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.apipermissionvalidator.ApiPermissionUtility;
import com.synoriq.synofin.apipermissionvalidator.dto.request.ApiMigrationDetails;
import com.synoriq.synofin.apipermissionvalidator.dto.request.SyncApiEndpointsRequest;
import com.synoriq.synofin.apipermissionvalidator.dto.response.SyncApiEndpointResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class ContextRefreshListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    ApiPermissionUtility apiPermissionUtility;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        List<RequestMappingInfo> requestMappingInfo = new ArrayList<>();
        List<ApiMigrationDetails> requestParams = new ArrayList<>();
        ApplicationContext applicationContext = event.getApplicationContext();
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
                .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
                .getHandlerMethods();
        map.forEach((key, value) -> {

            if(!key.getMethodsCondition().isEmpty() && !key.getMethodsCondition().isEmpty()) {
                key.getMethodsCondition().getMethods().forEach(method -> {
                    key.getPatternsCondition().getPatterns().forEach(pattern -> {
                        ApiMigrationDetails requestElement = new ApiMigrationDetails();
                        requestElement.setServiceName(applicationName);
                        requestElement.setPrefix(applicationName);
                        requestElement.setApiType(method.name());
                        requestElement.setEndpoint(pattern);
                        requestElement.setPermissionName(pattern);// default permission name is the URL, can be changed manually later
                        requestParams.add(requestElement);
                    });
                });
            }
        });
        SyncApiEndpointsRequest apiRequest = new SyncApiEndpointsRequest();
        apiRequest.setData(requestParams);
        SyncApiEndpointResponse response = apiPermissionUtility.syncApis(apiRequest);
        log.info("total synched APIs = {}", response.getMigratedApiCount());
    }
}
