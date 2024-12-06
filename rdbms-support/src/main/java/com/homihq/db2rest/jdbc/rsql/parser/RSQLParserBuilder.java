package com.homihq.db2rest.jdbc.rsql.parser;

import com.homihq.db2rest.jdbc.rsql.operator.CustomRSQLOperators;
import cz.jirutka.rsql.parser.RSQLParser;

public class RSQLParserBuilder {

    public static RSQLParser newRSQLParser() {
        return new RSQLParser(CustomRSQLOperators.customOperators());
    }

}
