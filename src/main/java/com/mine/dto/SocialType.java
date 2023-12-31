package com.mine.dto;

public enum SocialType {

	FACEBOOK("facebook"), GOOGLE("google"), LOCAL("local");
	 
    private String provider;
 
    public String getProvider() {
        return provider;
    }
 
    SocialType(final String provider) {
        this.provider = provider;
    }
	
}
