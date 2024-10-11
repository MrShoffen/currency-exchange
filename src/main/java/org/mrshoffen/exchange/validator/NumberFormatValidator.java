package org.mrshoffen.exchange.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class NumberFormatValidator implements ConstraintValidator<ValidNumberFormat, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
//        context.disableDefaultConstraintViolation();


        try {
            BigDecimal value = new BigDecimal(s);

            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                context.buildConstraintViolationWithTemplate("Number must be positive!")
                        .addConstraintViolation();
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
