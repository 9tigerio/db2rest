package com.homihq.db2rest.core.util;

import com.homihq.db2rest.core.exception.InvalidPaginationParameterException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaginationValidatorTest {

    @Test
    void shouldAcceptDefaultPaginationValues() {
        assertThatNoException()
                .isThrownBy(() -> PaginationValidator.validate(-1, -1));
    }

    @Test
    void shouldAcceptExplicitLimitAndOffset() {
        assertThatNoException()
                .isThrownBy(() -> PaginationValidator.validate(10, 0));
    }

    @Test
    void shouldRejectZeroLimit() {
        assertThatThrownBy(() -> PaginationValidator.validate(0, -1))
                .isInstanceOf(InvalidPaginationParameterException.class)
                .hasMessageContaining("limit");
    }

    @Test
    void shouldRejectLimitLessThanMinusOne() {
        assertThatThrownBy(() -> PaginationValidator.validate(-2, -1))
                .isInstanceOf(InvalidPaginationParameterException.class)
                .hasMessageContaining("limit");
    }

    @Test
    void shouldRejectOffsetLessThanMinusOne() {
        assertThatThrownBy(() -> PaginationValidator.validate(-1, -2))
                .isInstanceOf(InvalidPaginationParameterException.class)
                .hasMessageContaining("offset");
    }
}
