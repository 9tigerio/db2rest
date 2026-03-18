package com.homihq.db2rest.jdbc.validator;

import com.homihq.db2rest.jdbc.validator.impl.IsRequiredValidator;
import com.homihq.db2rest.jdbc.validator.impl.IsUUIDValidator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomPlaceholderValidators {
    public static final String IS_REQUIRED = "is_required";
    public static final String IS_UUID = "is_uuid";

    private final Map<String, ConstraintValidator> validators;

    public CustomPlaceholderValidators() {
        Map<String, ConstraintValidator> map = new HashMap<>();
        map.put(IS_REQUIRED, new IsRequiredValidator());
        map.put(IS_UUID, new IsUUIDValidator());
        this.validators = Collections.unmodifiableMap(map);
    }

    public ConstraintValidator getValidator(String constraint) {
        return validators.get(constraint);
    }

    public Map<String, ConstraintValidator> getValidators() {
        return validators;
    }
}
