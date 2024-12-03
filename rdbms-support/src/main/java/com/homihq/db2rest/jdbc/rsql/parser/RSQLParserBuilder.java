package com.homihq.db2rest.jdbc.rsql.parser;

import com.homihq.db2rest.jdbc.rsql.operator.CustomRSQLOperators;
import cz.jirutka.rsql.parser.RSQLParser;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RSQLParserBuilder {

    public RSQLParser newRSQLParser() {
        return new RSQLParser(CustomRSQLOperators.customOperators());
    }

}
