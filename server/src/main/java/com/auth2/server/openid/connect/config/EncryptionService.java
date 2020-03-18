package com.auth2.server.openid.connect.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import org.mitre.jose.keystore.JWKSetKeyStore;
import org.mitre.jwt.encryption.service.impl.DefaultJWTEncryptionAndDecryptionService;
import org.mitre.jwt.signer.service.impl.DefaultJWTSigningAndValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author baizh@enlink.cn
 * @date 2020/3/10
 */

@Configuration
public class EncryptionService {

    private Logger LOGGER = LoggerFactory.getLogger(EncryptionService.class);

    @Bean
    public DefaultJWTEncryptionAndDecryptionService defaultEncryptionService(JWKSetKeyStore defaultKeyStore) {
        DefaultJWTEncryptionAndDecryptionService jwtEncryptionAndDecryptionService;
        try {
            jwtEncryptionAndDecryptionService = new DefaultJWTEncryptionAndDecryptionService(defaultKeyStore);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | JOSEException e) {
            LOGGER.error("Invalid JWKSetKeyStore!", e);
            throw new RuntimeException(e);
        }

        jwtEncryptionAndDecryptionService.setDefaultAlgorithm(JWEAlgorithm.RSA1_5);
        jwtEncryptionAndDecryptionService.setDefaultDecryptionKeyId("RSA1_5");
        jwtEncryptionAndDecryptionService.setDefaultEncryptionKeyId("rsa1");
        return jwtEncryptionAndDecryptionService;
    }

    @Bean
    public JWKSetKeyStore defaultKeyStore() {
        JWKSetKeyStore jwkSetKeyStore = new JWKSetKeyStore();
        jwkSetKeyStore.setLocation(new ClassPathResource("keystore.jwks"));
        return jwkSetKeyStore;
    }

    @Bean
    public DefaultJWTSigningAndValidationService defaultSignerService(JWKSetKeyStore defaultKeyStore) {
        DefaultJWTSigningAndValidationService jwtSigningAndValidationService;
        try {
            jwtSigningAndValidationService = new DefaultJWTSigningAndValidationService(defaultKeyStore);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.error("Invalid JWKSetKeyStore!", e);
            throw new RuntimeException(e);
        }

        jwtSigningAndValidationService.setDefaultSignerKeyId("rsa1");
        jwtSigningAndValidationService.setDefaultSigningAlgorithmName("RS256");
        return jwtSigningAndValidationService;
    }
}
