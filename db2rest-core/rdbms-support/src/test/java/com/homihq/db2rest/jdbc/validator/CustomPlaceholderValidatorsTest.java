package com.homihq.db2rest.jdbc.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomPlaceholderValidatorsTest {

    private CustomPlaceholderValidators customPlaceholderValidators;

    @BeforeEach
    void setUp() {
        customPlaceholderValidators = new CustomPlaceholderValidators();
    }

    @Test
    void shouldReturnIsRequiredValidator() {
        ConstraintValidator validator = customPlaceholderValidators
            .getValidator(CustomPlaceholderValidators.IS_REQUIRED);
        assertThat(validator).isNotNull();
    }

    @Test
    void shouldReturnIsUUIDValidator() {
        ConstraintValidator validator = customPlaceholderValidators
            .getValidator(CustomPlaceholderValidators.IS_UUID);
        assertThat(validator).isNotNull();
    }

    @Test
    void shouldReturnNullForUnknownConstraint() {
        ConstraintValidator validator = customPlaceholderValidators
            .getValidator("unknown_constraint");
        assertThat(validator).isNull();
    }

    @Test
    void shouldReturnUnmodifiableValidatorsMap() {
        assertThatThrownBy(() ->
            customPlaceholderValidators.getValidators().put("new", null))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void isRequiredConstantShouldBeCorrect() {
        assertThat(CustomPlaceholderValidators.IS_REQUIRED)
            .isEqualTo("is_required");
    }

    @Test
    void isUUIDConstantShouldBeCorrect() {
        assertThat(CustomPlaceholderValidators.IS_UUID)
            .isEqualTo("is_uuid");
    }
} 

