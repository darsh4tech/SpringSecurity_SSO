package com.mine.validator;

import com.mine.dto.SignUpRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, SignUpRequest>{

	@Override
	public boolean isValid(SignUpRequest user, ConstraintValidatorContext context) {
		return user.getPassword().equals(user.getMatchingPassword());
	}

}
