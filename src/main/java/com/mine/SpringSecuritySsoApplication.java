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
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mine.dto.SocialType;
import com.mine.dto.UserRoleEnum;
import com.mine.entity.AppUser;
import com.mine.entity.UserStatus;
import com.mine.entity.auditing.AuditorAwareImpl;
import com.mine.repo.UserRepository;

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
	
	// to save SecurityContext between requests as default saving like previous spring security versions (5.x) is removed in current spring security version (6.x)
	@Bean
	SecurityContextRepository securityContextRepository() {
		return new RequestAttributeSecurityContextRepository();
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
