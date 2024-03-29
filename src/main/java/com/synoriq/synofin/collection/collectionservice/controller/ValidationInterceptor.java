//package com.synoriq.synofin.collection.collectionservice.controller;
//
//import com.synoriq.synofin.apipermissionvalidator.ApiPermissionUtility;
//import com.synoriq.synofin.apipermissionvalidator.exceptions.ApiPermissionValidationException;
//import com.synoriq.synofin.collection.collectionservice.apiPermissionValidators.PermissionValidators;
//import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
//import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
//import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartHttpServletRequest;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.net.URI;
//import java.util.Map;
//@Component
//@Slf4j
//public class ValidationInterceptor implements HandlerInterceptor {
//    @Autowired
//    CurrentUserInfo currentUserInfo;
//    @Autowired
//    ApiPermissionUtility apiValidationUtility;
//    @Override
//    public boolean preHandle(
//            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        try {
//            String apiType = request.getMethod();
//            if(apiType == null) {
//                log.info("API allowed because API type not found");
//                return true; // unable to handle requests without api type
//            }
//            MediaType contentType = null;
//            String url = request.getRequestURL().toString();
//            String requestPath = URI.create(url).getPath();
//            String tokenDetail = request.getHeader("Authorization");
//            if(tokenDetail == null) {
//                log.warn("Auth token is mandatory");
//                throw new CustomException(ErrorCode.UNAUTHORIZED_API_TRANSACTION);
//            }
//            String authToken = tokenDetail.replace("Bearer ", "");
//            Map<String, String[]> queryParams = request.getParameterMap();
//            long contentLength = request.getContentLength();
//            log.info("Request URL is - {}", url);
//            log.info("Content length is - {}", contentLength);
//            if(contentLength <= 0) {
//                try {
//                    boolean validationResponse = apiValidationUtility.checkApiValidation(
//                            PermissionValidators.class,
//                            null,
//                            queryParams,
//                            requestPath,
//                            authToken,
//                            apiType);
//                    if(!validationResponse) {
//                        log.error("Error while validating API for requests without content length");
//                        throw new CustomException(ErrorCode.UNAUTHORIZED_API_TRANSACTION);
//                    }
//                } catch (ApiPermissionValidationException e) {
//                    e.printStackTrace();
//                    log.error("Exception while trying to to validate API - {}", e.getMessage());
//                    throw new CustomException(ErrorCode.UNAUTHORIZED_API_TRANSACTION);
//                }
//                log.info("API allowed for Bodyless request");
//                return true;
//            }
//            if(request.getContentType() == null) {
//                log.info("API allowed because content type is null");
//                return true;
//            }
//            try {
//                contentType = MediaType.parseMediaType(request.getContentType());
//            } catch (Exception e) {
//                log.info("API allowed because media type cannot be parsed - {}", e);
//                return true;
//            }
//            if(contentType == null) {
//                log.info("API allowed because content media type is null");
//                return true; // content type cannot be null, and be handled by api validator
//            }
//            if(apiType.equals("POST") && contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
//                return true; // this case is handled using controller advice
//            }
//            if(!isMediaTypeAllowed(contentType)) {
//                return true;
//            }
//            if(request instanceof MultipartHttpServletRequest) {
//                // not processing request body in case of multipart
//                try {
//
//                    boolean validationResponse = apiValidationUtility.checkApiValidation(
//                            PermissionValidators.class,
//                            null,
//                            queryParams,
//                            requestPath,
//                            authToken,
//                            apiType);
//                    if(!validationResponse) {
//                        throw new CustomException(ErrorCode.UNAUTHORIZED_API_TRANSACTION);
//                    }
//                } catch (ApiPermissionValidationException e) {
//                    e.printStackTrace();
//                    log.error("Unable to validate API - {}", e.getMessage());
//                    throw new CustomException(ErrorCode.UNAUTHORIZED_API_TRANSACTION);
//                }
//            }
//            log.info("API allowed with request with body");
//            return true;
//        } catch (Exception ee) {
//            ee.printStackTrace();
//            log.error("Error received in API security - {}", ee);
//            throw new CustomException(ErrorCode.UNAUTHORIZED_API_TRANSACTION);
//        }
//
//    }
//
//    private boolean isMediaTypeAllowed(MediaType mediaType) {
//        if( mediaType.isCompatibleWith(MediaType.APPLICATION_JSON) ||
//                mediaType.isCompatibleWith(MediaType.MULTIPART_FORM_DATA)
//        ) {
//            return true;
//        }
//        return false;
//
//    }
//}
