package com.auth2.server.config;

import org.mitre.oauth2.service.OAuth2TokenEntityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import javax.annotation.Resource;

/**
 * @author baizh@enlink.cn
 * @date 2020/3/11
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Resource(name = "defaultOAuth2ProviderTokenService")
    private OAuth2TokenEntityService tokenService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers().antMatchers("/api/**",
                "/userinfo**",
                "/resource/**",
                "/register/**",
                "/user/**")
                .and()
                .authorizeRequests().antMatchers("/api/**",
                "/userinfo**",
                "/resource/**",
                "/register/**",
                "/user/**").authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenServices(tokenService);
    }
}
