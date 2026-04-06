package com.stefano.bff_viacep.validation.validator;

import com.stefano.bff_viacep.validation.annotation.CepValido;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CepValitator implements ConstraintValidator<CepValido, String> {
    private static final String CEP_REGEX = "\\d{8}";

    @Override
    public boolean isValid(String cep, ConstraintValidatorContext context) {
        if (cep == null || cep.isBlank()) {
            return false;
        }
        return cep.matches(CEP_REGEX);
    }
}
