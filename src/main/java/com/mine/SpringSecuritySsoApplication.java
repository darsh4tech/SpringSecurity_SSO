package com.mine;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mine.dto.SocialType;
import com.mine.dto.UserRoleEnum;
import com.mine.entity.AppUser;
import com.mine.entity.UserStatus;
import com.mine.model.auditing.AuditorAwareImpl;
import com.mine.repo.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@SpringBootApplication
public class SpringSecuritySsoApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
  
	public static void main(String[] args) {
		SpringApplication.run(SpringSecuritySsoApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	ObjectMapper objectMapper() {
		return new ObjectMapper(); 
	}

	@Bean
    AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
	
	@Bean
	protected HandlerInterceptor webRequestHandlerInterceptorAdapter() {
		
		return new HandlerInterceptor() {
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
					throws Exception {
				response.setHeader("Access-Control-Allow-Origin", "*");
				response.setHeader("Access-Control-Allow-Methods", "GET");
				response.setHeader("Access-Control-Max-Age", "3600");
				response.setHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept, Authorization");
				return true;
			}
		}; 		
	}
	
	
	@Override
	public void run(String... args) throws Exception {
		if(userRepository.findByEmail("admin@test.com").isEmpty())
			createUserIfNotFound("admin@test.com", Set.of(UserRoleEnum.ROLE_USER.name(), UserRoleEnum.ROLE_ADMIN.name(), UserRoleEnum.ROLE_MODERATOR.name()));		
	}

    @Transactional
    private final void createUserIfNotFound(final String email, Set<String> roles) {
    	userRepository.findByEmail(email).orElseGet(() -> {
    		AppUser user = new AppUser();
    		user.setUserName("Admin");
    		user.setEmail(email);
    		user.setPassword(new BCryptPasswordEncoder().encode("123456"));
    		user.setRoles(roles);
    		user.setStatus(UserStatus.ENABLED);
    		user.setSocialType(SocialType.LOCAL);
    		user = userRepository.save(user);
    		return user;
    	});
//        return user;
    } 

}
