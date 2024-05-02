package com.homihq.db2test.mongo.rsql.argconverters;

import com.homihq.db2test.mongo.rsql.structs.ConversionInfo;
import com.homihq.db2test.mongo.rsql.structs.Lazy;

public class NoOpConverter implements StringToQueryValueConverter {

    @Override
    public Lazy<Object> convert(ConversionInfo info) {
        return Lazy.fromValue(info.argument());
    }

}
