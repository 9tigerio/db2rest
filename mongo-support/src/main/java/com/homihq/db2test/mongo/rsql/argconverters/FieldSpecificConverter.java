package com.homihq.db2test.mongo.rsql.argconverters;

import com.homihq.db2test.mongo.rsql.structs.ConversionInfo;
import com.homihq.db2test.mongo.rsql.structs.Lazy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
public class FieldSpecificConverter implements StringToQueryValueConverter {

    private final Class<?> objectClass;
    private final String pathToField;
    private final Function<String, Object> converter;

    @Override
    public Lazy<Object> convert(final ConversionInfo info) {
        return Lazy.fromFunc(() -> {
            if (info.targetEntityClass().equals(objectClass)
                    && info.pathToField().equals(pathToField)
            ) {
                return converter.apply(info.argument());
            }

            return null;
        });
    }

}
