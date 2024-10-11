package org.mrshoffen.exchange.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.HibernateValidator;

import java.util.Set;

@Value
@Builder
public class CurrencyRequestDto {

    @NotBlank
    String code;


    String name;


    String sign;

    public static void main(String[] args) {

        Validator validator = Validation.byProvider(HibernateValidator.class)
                .configure()
                .buildValidatorFactory().getValidator();

        CurrencyRequestDto hfd = CurrencyRequestDto.builder()
                .code("   ")
                .build();

        Set<ConstraintViolation<CurrencyRequestDto>> validate = validator.validate(hfd);

        System.out.println(validate.size());
    }
}
