package ru.yandex.practicum.model.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface ValidReleaseDate {
    String message() default "Invalid release date. The date should not be before 28 December 1895.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}