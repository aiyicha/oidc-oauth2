package com.auth2.server.openid.connect.config;

import org.mitre.jwt.assertion.impl.NullAssertionValidator;
import org.mitre.jwt.assertion.impl.WhitelistedIssuerAssertionValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author baizh@enlink.cn
 * @date 2020/3/10
 */
@Configuration
public class JWTConfiguration {

    @Bean
    public NullAssertionValidator jwtAssertionValidator() {
        return new NullAssertionValidator();
    }

    @Bean
    public WhitelistedIssuerAssertionValidator clientAssertionValidator() {
        WhitelistedIssuerAssertionValidator whitelistedIssuerAssertionValidator = new WhitelistedIssuerAssertionValidator();
        Map<String, String> map = new HashMap<>();
        map.put("http://artemesia.local", "http://localhost:8080/openid-connect-server-webapp/jwk");
        whitelistedIssuerAssertionValidator.setWhitelist(map);
        return whitelistedIssuerAssertionValidator;
    }
}
