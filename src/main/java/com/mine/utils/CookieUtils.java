package com.mine.utils;

import java.io.Serializable;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieUtils {
		
	public Optional<Cookie> getCookie(HttpServletRequest request, String name) {
				
		if (request.getCookies() != null && request.getCookies().length > 0) {
			return Stream.of(request.getCookies()).filter(cookie -> cookie.getName().equals(name)).findFirst(); 
		}
		return Optional.empty();
	}

	public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					cookie.setValue("");
					cookie.setPath("/");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
		}
	}
	
	
	public String serialize(Serializable object) {
		
		return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
	}

//	@SneakyThrows
	public <T> T deserialize(Cookie cookie, Class<T> cls) {
//		log.info("cookie.getValue() : {}",cookie.getValue());
//		log.info("Base64.getUrlDecoder().decode(cookie.getValue()) : {}",Base64.getUrlDecoder().decode(cookie.getValue()));
//		return objectMapper.readValue(Base64.getUrlDecoder().decode(cookie.getValue()), cls);
		return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue()))); 
	}
	
}
