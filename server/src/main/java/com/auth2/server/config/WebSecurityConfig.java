package com.auth2.server.config;

import com.auth2.server.openid.connect.assertion.JWTBearerAuthenticationProvider;
import com.auth2.server.openid.connect.assertion.JWTBearerClientAssertionTokenEndpointFilter;
import com.auth2.server.openid.connect.filter.MultiUrlRequestMatcher;
import com.auth2.server.openid.connect.service.impl.DefaultUserInfoService;
import com.auth2.server.service.MyUserDetailsService;
import com.auth2.server.service.OwnUserDetailsService;
import org.mitre.oauth2.service.OAuth2TokenEntityService;
import org.mitre.openid.connect.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author baizh@enlink.cn
 * @date 2020/3/2
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private OwnUserDetailsService ownUserDetailsService;

    @Resource(name="clientUserDetailsService")
    private UserDetailsService clientUserDetailsService;

    @Resource(name="uriEncodedClientUserDetailsService")
    private UserDetailsService uriEncodedClientUserDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(ownUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/assets/**", "/css/**", "/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http

//                .and()
//                .antMatcher("/devicecode/**")

//                .addFilterAfter(clientAssertionEndpointFilter(), AbstractPreAuthenticatedProcessingFilter.class)
//                .addFilterAfter(clientCredentialsEndpointFilter(), BasicAuthenticationFilter.class)

//                .antMatcher("/revoke**")
//                .addFilterAfter(clientAssertionEndpointFilter(), AbstractPreAuthenticatedProcessingFilter.class)
//                .addFilterAfter(clientCredentialsEndpointFilter(), BasicAuthenticationFilter.class)
//                .antMatcher("/introspect**")
                .addFilterAfter(clientAssertionEndpointFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterAfter(clientCredentialsEndpointFilter(), BasicAuthenticationFilter.class)
                .formLogin()
                .loginPage("/login")
                .and()
                .authorizeRequests()
//                .antMatchers("/authorize").hasRole("USER")
                .antMatchers("/login",
                        "/.well-known/**",
                        "/jwk**",
                        "/cas/**")
                .permitAll()

                //.and().requestMatchers(new AntPathRequestMatcher("/devicecode/**", "/revoke**", "/introspect**"))

                .anyRequest().authenticated()
                .and().csrf().disable().cors()
        ;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ClientCredentialsTokenEndpointFilter clientCredentialsEndpointFilter() {
        ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter = new ClientCredentialsTokenEndpointFilter();
        DaoAuthenticationProvider daoAuthenticationProvider1 = new DaoAuthenticationProvider();
        daoAuthenticationProvider1.setUserDetailsService(clientUserDetailsService);
        DaoAuthenticationProvider daoAuthenticationProvider2 = new DaoAuthenticationProvider();
        daoAuthenticationProvider2.setUserDetailsService(uriEncodedClientUserDetailsService);
        clientCredentialsTokenEndpointFilter.setAuthenticationManager(
                new ProviderManager(Arrays.asList(daoAuthenticationProvider1, daoAuthenticationProvider2)));
        clientCredentialsTokenEndpointFilter.setRequiresAuthenticationRequestMatcher(clientAuthMatcher());
        return clientCredentialsTokenEndpointFilter;
    }

    @Bean
    public MultiUrlRequestMatcher clientAuthMatcher() {
        Set<String> filterProcessesUrls = new HashSet<>();
        filterProcessesUrls.add("/introspect");
        filterProcessesUrls.add("/revoke");
        filterProcessesUrls.add("/token");
        return new MultiUrlRequestMatcher(filterProcessesUrls);
    }

    @Bean
    public JWTBearerClientAssertionTokenEndpointFilter clientAssertionEndpointFilter() {
        JWTBearerClientAssertionTokenEndpointFilter clientAssertionEndpointFilter =
                new JWTBearerClientAssertionTokenEndpointFilter(clientAuthMatcher());

        clientAssertionEndpointFilter.setAuthenticationManager(
                new ProviderManager(Arrays.asList(new JWTBearerAuthenticationProvider())));
        return clientAssertionEndpointFilter;
    }


}
