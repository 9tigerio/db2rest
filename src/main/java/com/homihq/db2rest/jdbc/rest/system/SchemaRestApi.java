package com.homihq.db2rest.jdbc.rest.system;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/schema")
public interface SchemaRestApi {


    @GetMapping(value = "/objects")
    List<TableObject> getObjects();
}
