package com.homihq.db2rest.core.exception;

import java.util.Map;

public class RpcException extends RuntimeException {

    public RpcException(String subRoutineName, Map<String, Object> inParams) {
        super("Procedure/Function name: "
                        + subRoutineName + ", IN parameters: "
                        + inParams.entrySet());
    }

}
