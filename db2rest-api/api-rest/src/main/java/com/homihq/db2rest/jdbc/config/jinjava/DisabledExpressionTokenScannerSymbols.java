package com.homihq.db2rest.jdbc.config.jinjava;

import com.hubspot.jinjava.tree.parse.DefaultTokenScannerSymbols;

public class DisabledExpressionTokenScannerSymbols extends DefaultTokenScannerSymbols {
    @Override
    public int getExprStart() {
        return 0;
    }

    @Override
    public int getExprEnd() {
        return 0;
    }
}
