package com.synoriq.synofin.collection.collectionservice.config.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Component
public class PermissionFilter extends OncePerRequestFilter {
//    we can exclude the APIs by mentioning the endpoints in the below array list
//    private static final Set<String> EXCLUDED_URLS = new HashSet<>(Arrays.asList("/api/public"));

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UtilityService utilityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//        We can compare the current API with the excluded APIs array

//        String requestUri = request.getRequestURI();
//        if (EXCLUDED_URLS.stream().anyMatch(requestUri::startsWith)) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            UserDetailByTokenDTOResponse permissions = utilityService.getUserDetailsByToken(authorizationHeader);
            if (hasPermission(permissions.getData().getUserData().getPermissions(), "collection_login")) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("error", false);
        responseData.put("response", true);
        responseData.put("data", "You don't have permission to access this resource");

        PrintWriter writer = response.getWriter();
        writer.print(objectMapper.writeValueAsString(responseData));
        writer.flush();
    }

    private boolean hasPermission(List<String> permissions, String requiredPermission) {
        return permissions.contains(requiredPermission);
    }
}
