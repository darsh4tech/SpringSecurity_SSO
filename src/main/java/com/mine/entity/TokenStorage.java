package com.mine.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table
@Data
@EntityListeners(AuditingEntityListener.class)
public class TokenStorage {

	@Id
    @GeneratedValue
	private Long id;
	
	private String refreshToken;
	
	private Long userId;
	
    @CreatedDate
    private LocalDateTime createdDate;

    private boolean softDelete;
    
}
