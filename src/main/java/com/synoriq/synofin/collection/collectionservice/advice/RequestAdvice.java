//package com.synoriq.synofin.collection.collectionservice.advice;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.synoriq.synofin.apipermissionvalidator.ApiPermissionUtility;
//import com.synoriq.synofin.collection.collectionservice.apiPermissionValidators.PermissionValidators;
//import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
//import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
//import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.HttpInputMessage;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
//
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.net.URI;
//import java.util.List;
//import java.util.Map;
//
//@ControllerAdvice
//@Slf4j
//public class RequestAdvice implements RequestBodyAdvice {
//    @Autowired
//    CurrentUserInfo currentUserInfo;
//    @Autowired
//    ApiPermissionUtility apiValidationUtility;
//
//    @Override
//    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
//        return true;
//    }
//
//    @Override
//    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
//        return httpInputMessage;
//    }
//
//    @Override
//    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
//        try {
//            ServletRequestAttributes requestAttribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            String requestBodyString = getRequestBody(o);
//            MediaType contentType = httpInputMessage.getHeaders().getContentType();
//            UrlAttributes urlAttributes = getUrlAttributes();
//            List<String> tokenStringList = httpInputMessage.getHeaders().get("Authorization");
//            if(tokenStringList.size() == 0) {
//                log.warn("Auth token is mandatory");
//                throw new CustomException(ErrorCode.UNAUTHORIZED_API_TRANSACTION);
//            }
//            String authToken = tokenStringList.get(0).replace("Bearer ", "");
//            Long contentLength = httpInputMessage.getHeaders().getContentLength();
//            if(!(contentType != null && contentType.isCompatibleWith(MediaType.APPLICATION_JSON))) {
//                return o;
//            }
////            if(contentLength <= 0) {
////                return o;
////            }
//            boolean response = apiValidationUtility.checkApiValidation(
//                    PermissionValidators.class,
//                    requestBodyString,
//                    urlAttributes.apiQueryParams,
//                    urlAttributes.urlPath,
//                    authToken,
//                    urlAttributes.apiType);
//            if(!response) {
//                throw new CustomException(ErrorCode.UNAUTHORIZED_API_TRANSACTION);
//            }
//            return o;
//        } catch (JsonProcessingException e) {
//            throw new CustomException(ErrorCode.UNAUTHORIZED_API_TRANSACTION);
//        } catch (Exception e) {
//            throw new CustomException(ErrorCode.UNAUTHORIZED_API_TRANSACTION);
//        }
//    }
//
//    @Override
//    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
//        return o;
//    }
//
//    private String getRequestBody(Object o) throws JsonProcessingException {
//        ObjectMapper om = new ObjectMapper();
//        return om.writeValueAsString(o);
//    }
//
//    private UrlAttributes getUrlAttributes() {
//        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes) {
//            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            UrlAttributes response = new UrlAttributes();
//            response.urlPath = URI.create(attributes.getRequest().getRequestURL().toString()).getPath();
//            response.apiType = attributes.getRequest().getMethod();
//            response.apiQueryParams = attributes.getRequest().getParameterMap();
//            try {
//            } catch (Exception e) {
//                // do nothing
//            }
//            return response;
//        } else {
//            throw new CustomException(ErrorCode.UNAUTHORIZED_API_TRANSACTION);
//        }
//    }
//
//    private class UrlAttributes {
//        String urlPath;
//        String apiType;
//        Map<String, String[]> apiQueryParams;
//    }
//}
