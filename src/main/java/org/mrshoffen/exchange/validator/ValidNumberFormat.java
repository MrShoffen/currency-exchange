package org.mrshoffen.exchange.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NumberFormatValidator.class)
public @interface ValidNumberFormat {
    String message() default "";

    Class<?>[] groups() default { };
//
    Class<? extends Payload>[] payload() default { };

}
