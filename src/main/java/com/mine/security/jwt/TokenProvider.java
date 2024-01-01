package com.mine.security.jwt;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.mine.config.AppProperties;
import com.mine.dto.LocalUser;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.SneakyThrows;

@Service
public class TokenProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
	
	private AppProperties appProperties;
	 
    public TokenProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
    }
     
	@SneakyThrows
	public String createToken(Authentication  authentication) {

		LocalUser userPrincipal = (LocalUser) authentication.getPrincipal();
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(userPrincipal.getAppUser().getUserId().toString())
				.issueTime(new Date(System.currentTimeMillis()))
				// 2 min
				.expirationTime(Date.from(Instant.now().plusSeconds(Long.valueOf(appProperties.getExpireTokenInSeconds()))))
				.build();
		JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();

		SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);
		signedJWT.sign(new MACSigner(getSigningKey()));
		return signedJWT.serialize();

	}

	@SneakyThrows
	public String generateRefreshToken(Authentication  authentication) {

		LocalUser userPrincipal = (LocalUser) authentication.getPrincipal();
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(userPrincipal.getAppUser().getEmail())
				.issueTime(new Date(System.currentTimeMillis()))
				// 30 min
				.expirationTime(Date.from(Instant.now().plusSeconds(Long.valueOf(appProperties.getExpireRefreshTokenInSeconds()))))
				.build();
		JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();

		SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);
		signedJWT.sign(new MACSigner(getSigningKey()));
		return signedJWT.serialize();
		
	}
 
	private SecretKey getSigningKey() {
		byte[] key = Decoders.BASE64.decode(appProperties.getTokenSecret());		
		return Keys.hmacShaKeyFor(key);
	}
    
    public Long getUserIdFromToken(String token) {
    	Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        return Long.parseLong(claims.getSubject());
    }
 
    public boolean validateToken(String authToken) throws Exception{
        try {
        	Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
            throw ex;
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
            throw ex;
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
            throw ex;
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
            throw ex;
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
            throw ex;
        }
    }
	
}
