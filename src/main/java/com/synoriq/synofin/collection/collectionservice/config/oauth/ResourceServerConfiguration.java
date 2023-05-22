package com.synoriq.synofin.collection.collectionservice.config.oauth;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    private static final String RESOURCE_ID = "common_resource";

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Bean
    public TokenStore redisTokenStore() {
        return new RedisTokenStore(connectionFactory);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenStore(redisTokenStore()).resourceId(RESOURCE_ID).stateless(false);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //http.antMatcher("/**").authorizeRequests().anyRequest().authenticated();

        http.csrf().disable()
                .authorizeRequests()
                //    .antMatchers("/**").access("hasRole('ADMIN')")
                .antMatchers("/v1/login/**", "/v1/getAccessToken/**", "/**").permitAll()
//                .antMatchers("/v1/login/**", "/v1/getAccessToken/**", "/v1/**").permitAll()
                .anyRequest().authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
        http.cors();

    }
}