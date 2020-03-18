package com.auth2.server.openid.connect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;

/**
 * @author baizh@enlink.cn
 * @date 2020/3/10
 */
@Configuration
public class OAuth2Configuration {

    @Bean
    public DefaultWebResponseExceptionTranslator oauth2ExceptionTranslator() {
        return new DefaultWebResponseExceptionTranslator();
    }
}
