package com.auth2.server.config;

import com.auth2.server.openid.connect.web.*;
import org.mitre.openid.connect.web.UserInfoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

	private final static String[] EXCLUDE_URL = {
		JWKSetPublishingEndpoint.URL+"**",
		DiscoveryEndpoint.WELL_KNOWN_URL+"/**",
		"/resources/**",
		"/static/**",
		"/templates/**",
		"/assets/**",
		"/css/**",
		"/images/**",
		"/token**",
		DynamicClientRegistrationEndpoint.URL+"/**",
		ProtectedResourceRegistrationEndpoint.URL+"/**",
		UserInfoEndpoint.URL+"**",
		RootController.API_URL+"/**",
		DeviceEndpoint.URL+"/**",
		IntrospectionEndpoint.URL+"**",
		RevocationEndpoint.URL+"**",
	};

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new UserInfoInterceptor())
			.addPathPatterns("/**")
			.excludePathPatterns(EXCLUDE_URL);
		registry.addInterceptor(new ServerConfigInterceptor())
				.addPathPatterns("/**")
				.excludePathPatterns(EXCLUDE_URL);
		super.addInterceptors(registry);
	}

	/**
	 * 解决跨域访问问题
	 *
	 * @param registry
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
			.allowCredentials(true)
			.maxAge(3600);
		super.addCorsMappings(registry);
	}

	/**
	 * 解决静态资源访问问题
	 *
	 * @param registry
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html")
			.addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/static/**")
			.addResourceLocations("classpath:/static/");
		registry.addResourceHandler("/templates/**")
			.addResourceLocations("classpath:/templates/")
			.addResourceLocations("classpath:/META-INF/resources/templates/");
		registry.addResourceHandler("/webjars/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/");
		super.addResourceHandlers(registry);
	}
}
