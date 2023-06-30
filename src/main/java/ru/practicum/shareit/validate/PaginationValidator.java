package ru.practicum.shareit.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PaginationValidator implements ConstraintValidator<ValidPaginationFrom,Integer > {
    @Override
    public void initialize(ValidPaginationFrom constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return integer == null || integer > 0;
    }
}
