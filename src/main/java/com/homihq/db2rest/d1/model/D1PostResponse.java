package com.homihq.db2rest.d1.model;

import java.util.List;


public record D1PostResponse(List<D1Result> result,List<Message> errors, List<Message> messages, boolean success) {
}


