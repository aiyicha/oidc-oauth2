package com.auth2.server.config;

import com.auth2.server.openid.connect.request.ConnectOAuth2RequestFactory;
import com.auth2.server.openid.connect.service.impl.DefaultOAuth2ClientDetailsEntityService;
import com.auth2.server.openid.connect.token.*;
import com.auth2.server.utils.JwtRsaUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import org.mitre.jwt.signer.service.JWTSigningAndValidationService;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.mitre.oauth2.service.OAuth2TokenEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.security.*;
import java.util.Arrays;

/**
 * @author baizh@enlink.cn
 * @date 2020/3/2
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DefaultOAuth2ClientDetailsEntityService clientService;

    @Resource(name = "connect0Auth2RequestFactory")
    private ConnectOAuth2RequestFactory connectOAuth2RequestFactory;

    @Resource(name = "defaultOAuth2AuthorizationCodeService")
    private AuthorizationCodeServices authorizationCodeServices;

    @Resource(name = "defaultOAuth2ProviderTokenService")
    private OAuth2TokenEntityService tokenService;

    @Resource(name = "chainedTokenGranter")
    private ChainedTokenGranter chainedTokenGranter;

    @Resource(name = "jwtAssertionTokenGranter")
    private JWTAssertionTokenGranter jwtAssertionTokenGranter;

    @Resource(name = "deviceTokenGranter")
    private DeviceTokenGranter deviceTokenGranter;

    @Resource(name = "tofuUserApprovalHandler")
    private TofuUserApprovalHandler tofuUserApprovalHandler;

    @Autowired
    private JWTSigningAndValidationService jwtService;


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients();
        security.tokenKeyAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientService);
        //clients.jdbc(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//        endpoints.accessTokenConverter(jwtAccessTokenConverter());
        endpoints.tokenStore(jwtTokenStore());

        endpoints.authorizationCodeServices(authorizationCodeServices);
        endpoints.requestFactory(connectOAuth2RequestFactory);
        endpoints.requestValidator(oauthRequestValidator());
        //endpoints.pathMapping("/oauth/authorize", "/authorize");
        //endpoints.pathMapping("/oauth/authorize", "/oauth/authorize");
        endpoints.setClientDetailsService(clientService);
        endpoints.tokenServices(tokenService);
        endpoints.userApprovalHandler(tofuUserApprovalHandler);

        CompositeTokenGranter compositeTokenGranter =
                new CompositeTokenGranter(Arrays.asList(chainedTokenGranter,
                        jwtAssertionTokenGranter,
                        deviceTokenGranter,
                        endpoints.getTokenGranter()));

        endpoints.tokenGranter(compositeTokenGranter);
    }

    @Bean
    public ScopeServiceAwareOAuth2RequestValidator oauthRequestValidator() {
        return new ScopeServiceAwareOAuth2RequestValidator();
    }

    /*@Primary
    @Bean
    public DefaultTokenServices defaultTokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(jwtTokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }*/

    @Bean
    public JwtTokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
//        jwtAccessTokenConverter.setSigningKey("cjs");   //  Sets the JWT signing key.  MACSigner Symmetric Encryption
//        jwtAccessTokenConverter.setKeyPair(JwtRsaUtil.getInstance().getKeyPair("private"));
        jwtAccessTokenConverter.setKeyPair(jwksToKeyPair()); // RSA Asymmetric Encryption
        return jwtAccessTokenConverter;
    }

    private KeyPair jwksToKeyPair() {
        JWK jwk = jwtService.getKeys().get(jwtService.getDefaultSignerKeyId());
        RSAKey rsaKey = (RSAKey)jwk;
        PrivateKey privateKey;
        PublicKey publicKey;
        try {
            privateKey = rsaKey.toPrivateKey();
            publicKey = rsaKey.toPublicKey();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return new KeyPair(publicKey, privateKey);
    }

    private KeyPair generateKeyPair() {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        //利用上面的随机数据源初始化这个KeyPairGenerator对象
        //keyPairGenerator.initialize(KEYSIZE, secureRandom);
        keyPairGenerator.initialize(2048);
        // 生成密匙对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        return keyPair;
    }

}
