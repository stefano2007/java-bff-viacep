package com.stefano.bff_viacep.validation.annotation;


import com.stefano.bff_viacep.validation.validator.CepValitator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CepValitator.class)
@Documented
public @interface CepValido {

    String message() default "CEP inválido. Deve conter 8 numeros.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
