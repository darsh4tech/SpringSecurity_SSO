package com.mine.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mine.dto.SocialType;
import com.mine.entity.AppUser;


@Repository
public interface UserRepository extends JpaRepository<AppUser, Long>{

    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByEmailAndSocialType(String email, SocialType socialType);
 
}
