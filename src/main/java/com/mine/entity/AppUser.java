package com.mine.entity;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.mine.dto.SocialType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table
@Data
@EntityListeners(AuditingEntityListener.class)
public class AppUser {

	@Id
    @GeneratedValue
    private Long userId;
	
	@NotBlank
	@Email
    private String email;
 
    @Enumerated(EnumType.STRING)
    private UserStatus status;
 
    private String userName;

	@NotBlank
    private String password;
        
    private String socialUserId;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    
    @CreatedBy
    private String createdBy;
    
    @CreatedDate
    private LocalDateTime createdDate;
 
    @LastModifiedBy
    private String updatedBy;
    
    private LocalDateTime lastLoginDate;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<String> roles;

}
