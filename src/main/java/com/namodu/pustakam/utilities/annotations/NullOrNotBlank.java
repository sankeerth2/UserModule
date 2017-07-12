package com.namodu.pustakam.utilities.annotations;

import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Null;
import java.lang.annotation.*;

/**
 * Created by sanemdeepak on 7/18/16.
 */
@ConstraintComposition(CompositionType.OR)
@Null(message = "")
@NotBlank
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Documented
public @interface NullOrNotBlank {
    String message() default "Validation for an optional numeric field failed.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {  };
}
