package com.mine.config;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

@Configuration
public class WebConfig {
	
	@Value("${allowed.origins}")
	private String allowedOrigins;
	
	@Bean
	protected WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				final long MAX_AGE_SECS = 3600;
				registry.addMapping("/**")
						.allowedOrigins(allowedOrigins)
						.allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
						.maxAge(MAX_AGE_SECS);
			}

			@Override
			public Validator getValidator() {
				LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
				validator.setValidationMessageSource(messageSource());
				return validator;
			}
		};
	}

	@Bean
	protected MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	protected LocaleResolver localeResolver() {
		final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
		cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
		cookieLocaleResolver.setDefaultTimeZone(TimeZone.getTimeZone(ZoneId.of("Africa/Cairo")));
		return cookieLocaleResolver;
	}
		
}
