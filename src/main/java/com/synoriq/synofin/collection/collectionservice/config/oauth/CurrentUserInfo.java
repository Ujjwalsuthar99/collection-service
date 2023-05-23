package com.synoriq.synofin.collection.collectionservice.config.oauth;

import com.synoriq.synofin.collection.collectionservice.config.DatabaseContextHolder;
import com.synoriq.synofin.master.entity.usersmgmt.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

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
            log.info("get client id function if block");
            principal = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            return principal.getOAuth2Request().getClientId();
        } else {
            log.info("Get client else block");
            return DatabaseContextHolder.getEnvironment();
//            return tokenClients.get(Thread.currentThread().getName());
        }
    }
}
