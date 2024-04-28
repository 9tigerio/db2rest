package com.homihq.db2rest.mongo.rsql.argconverters;

import com.homihq.db2rest.mongo.rsql.structs.ConversionInfo;
import com.homihq.db2rest.mongo.rsql.structs.Lazy;

public class OperatorSpecificConverter implements StringToQueryValueConverter {

    @Override
    public Lazy<Object> convert(ConversionInfo info) {
        return switch (info.operator()) {
            case REGEX -> Lazy.fromValue(info.argument());
            case EXISTS -> Lazy.fromValue(Boolean.valueOf(info.argument()));
            default -> Lazy.empty();
        };
    }

}