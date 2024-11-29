package com.homihq.db2rest.jdbc.rest.meta.db;


import com.homihq.db2rest.jdbc.JdbcManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
public class DbInfoController implements DbInfoRestApi {

    private final JdbcManager jdbcManager;

    @Override
    public List<DbInfoObject> getObjects() {
        List<DbInfoObject> dbInfoObjects = new ArrayList<>();
        jdbcManager.getDbMetaMap().forEach(
                (k, v) -> dbInfoObjects.add(new DbInfoObject(k, v.productName(), v.majorVersion(), v.driverName(), v.driverVersion()))
        );

        return dbInfoObjects;
    }



}
