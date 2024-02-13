package com.homihq.db2rest.rsql2.parser;

import com.homihq.db2rest.rsql2.operator.CustomRSQLOperators;

import cz.jirutka.rsql.parser.RSQLParser;

public class RSQLParserBuilder {

    public static RSQLParser newRSQLParser(){
        return new RSQLParser(CustomRSQLOperators.customOperators());
    }

}
