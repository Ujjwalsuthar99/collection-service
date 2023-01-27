package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.master.entity.usersmgmt.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.tokenClients;

@Service
@Slf4j
public class CurrentUserInfo {
    private UserEntity user;

    public UserEntity getCurrentUser() {
        if (user == null) {
            OAuth2Authentication principal = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            log.info(principal.getOAuth2Request().getClientId());
        }
        return user;
    }

    public synchronized String getClientId() {
        OAuth2Authentication principal;
        if (SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2Authentication) {
            principal = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            return principal.getOAuth2Request().getClientId();
        } else {
            return tokenClients.get(Thread.currentThread().getName());
        }
    }
}
