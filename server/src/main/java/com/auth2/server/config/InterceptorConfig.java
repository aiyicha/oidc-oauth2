package com.auth2.server.config;

import com.auth2.server.openid.connect.web.*;
import org.mitre.openid.connect.web.UserInfoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

	private final static String[] EXCLUDE_URL = {
		"/" + JWKSetPublishingEndpoint.URL+"**",
		"/" + DiscoveryEndpoint.WELL_KNOWN_URL+"/**",
		"/resources/**",
		"/token**",
		"/login",
		"/" + DynamicClientRegistrationEndpoint.URL+"/**",
		"/" + ProtectedResourceRegistrationEndpoint.URL+"/**",
		"/" + UserInfoEndpoint.URL+"**",
		"/" + RootController.API_URL+"/**",
		"/" + DeviceEndpoint.URL+"/**",
		"/" + IntrospectionEndpoint.URL+"**",
		"/" + RevocationEndpoint.URL+"**"
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

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//重写这个方法，映射静态资源文件
		registry.addResourceHandler("/**")
				.addResourceLocations("classpath:/resources/")
				.addResourceLocations("classpath:/static/")
				.addResourceLocations("classpath:/public/");
		super.addResourceHandlers(registry);
	}

}
