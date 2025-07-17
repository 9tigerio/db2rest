package com.homihq.db2rest.jdbc.validator;

import com.homihq.db2rest.jdbc.validator.impl.IsRequiredValidator;
import com.homihq.db2rest.jdbc.validator.impl.IsUUIDValidator;

import java.util.HashMap;
import java.util.Map;

public class CustomPlaceholderValidators {
    public static final String IS_REQUIRED = "is_required";
    public static final String IS_UUID = "is_uuid";

    private final Map<String, ConstraintValidator> validators = new HashMap<>();

    public CustomPlaceholderValidators() {
        validators.put(IS_REQUIRED, new IsRequiredValidator());
        validators.put(IS_UUID, new IsUUIDValidator());
    }

    public ConstraintValidator getValidator(String constraint) {
        return validators.get(constraint);
    }
}
