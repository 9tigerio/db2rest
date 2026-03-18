package com.homihq.db2rest.jdbc.rsql.operator;

import com.db2rest.jdbc.dialect.Dialect;
import com.db2rest.jdbc.dialect.model.DbColumn;
import com.db2rest.jdbc.dialect.model.DbWhere;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OperatorHandlerTest {

    @Mock
    private Dialect dialect;

    @Mock
    private DbColumn dbColumn;

    @Mock
    private DbWhere dbWhere;

    private final OperatorHandler operatorHandler = 
        (d, col, where, value, type, paramMap) -> value;

    @Test
    void shouldAddNewKeyToParamMapWhenKeyDoesNotExist() {
        Map<String, Object> paramMap = new HashMap<>();
        String result = operatorHandler.reviewAndSetParam("name", "John", paramMap);

        assertThat(result).isEqualTo("name");
        assertThat(paramMap).containsKey("name");
        assertThat(paramMap.get("name")).isEqualTo("John");
    }

    @Test
    void shouldGenerateNewKeyWhenKeyAlreadyExists() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "existing");

        String result = operatorHandler.reviewAndSetParam("name", "John", paramMap);

        assertThat(result).isNotEqualTo("name");
        assertThat(result).startsWith("name_");
        assertThat(paramMap).containsKey(result);
    }

    @Test
    void maxParamSuffixConstantShouldBeTwenty() {
        assertThat(OperatorHandler.MAX_PARAM_SUFFIX).isEqualTo(20);
    }

    @Test
    void prefixConstantShouldBeColon() {
        assertThat(OperatorHandler.PREFIX).isEqualTo(":");
    }
}