package com.homihq.db2rest.mongo.rsql.argconverters;

import com.homihq.db2rest.mongo.rsql.structs.ConversionInfo;
import com.homihq.db2rest.mongo.rsql.structs.Lazy;

public class NoOpConverter implements StringToQueryValueConverter {

    @Override
    public Lazy<Object> convert(ConversionInfo info) {
        return Lazy.fromValue(info.argument());
    }

}