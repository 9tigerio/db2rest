package com.homihq.db2rest.core.exception;

public class DeleteOpNotAllowedException extends RuntimeException {


    public DeleteOpNotAllowedException(boolean safe) {
        super("Invalid delete operation , safe set to " + safe);
    }


}
