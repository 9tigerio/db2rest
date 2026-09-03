package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.core.exception.SqlTemplateNotFoundException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JinJavaTemplateExecutorServiceTest {

    private final JinJavaTemplateExecutorService templateExecutorService =
            new JinJavaTemplateExecutorService(null, null, null, null, null);

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ",
            ".hidden",
            "../secret",
            "..\\secret",
            "template..name",
            "/absolute/path",
            "nested/template",
            "nested\\template",
            "template name",
            "template%2Fname"
    })
    void rejectsUnsafeTemplateNames(String templateName) {
        assertThatThrownBy(() -> templateExecutorService.execute("db", templateName, Map.of()))
                .isInstanceOf(SqlTemplateNotFoundException.class);
    }
}
