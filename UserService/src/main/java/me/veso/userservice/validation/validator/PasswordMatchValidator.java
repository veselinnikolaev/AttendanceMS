package me.veso.userservice.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.veso.userservice.dto.UserRegisterDto;
import me.veso.userservice.validation.annotation.PasswordMatch;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserRegisterDto> {
    @Override
    public boolean isValid(UserRegisterDto userRegisterDto, ConstraintValidatorContext constraintValidatorContext) {
        String plainPassword = userRegisterDto.password();
        String repeatPassword = userRegisterDto.confirmPassword();

        return plainPassword != null && plainPassword.equals(repeatPassword);
    }
}
