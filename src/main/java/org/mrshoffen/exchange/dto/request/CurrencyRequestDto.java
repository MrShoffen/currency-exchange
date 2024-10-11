package org.mrshoffen.exchange.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.HibernateValidator;
import org.mrshoffen.exchange.validator.ValidIso4217;

import java.util.Set;

@Value
@Builder
public class CurrencyRequestDto {

    @NotBlank(message = "Currency code is empty!")
    @Size(min = 3, max = 3, message = "Currency code length must be 3 symbols!")
    @ValidIso4217(message = "Currency code not in 4217 ISO!")
    String code;

    @NotBlank(message = "Currency name is empty!")
    String name;

    @NotBlank(message = "Currency sign is empty!")
    String sign;

    public static void main(String[] args) {

    }
}
