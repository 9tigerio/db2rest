package com.homihq.db2test.mongo.rsql.argconverters;

import com.homihq.db2test.mongo.rsql.structs.ConversionInfo;
import com.homihq.db2test.mongo.rsql.structs.Lazy;

public interface StringToQueryValueConverter {

    Lazy<Object> convert(ConversionInfo info);

}
