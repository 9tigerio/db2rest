package com.homihq.db2rest.jdbc.tsid;

import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.core.model.DbColumn;
import io.hypersistence.tsid.TSID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TSIDProcessor {

    public Map<String,Object> processTsId(Map<String, Object> data, List<DbColumn> pkColumns) {
        log.info("PK Columns - {}", pkColumns);

        Map<String,Object> generatedKeys = new HashMap<>();

        for(DbColumn dbColumn : pkColumns) {

            log.info("isGenerated - {}", dbColumn.generated());
            log.info("isAutoIncremented - {}", dbColumn.autoIncremented());

            if(!dbColumn.generated() && !dbColumn.autoIncremented()) {
                //detect type

                log.info("detect type of the TSID column - {}", dbColumn.columnDataTypeName());

                if(dbColumn.isIntFamily()) {
                    log.debug("PK of Int family");
                    long tsId = TSID.Factory.getTsid().toLong();
                    data.put(dbColumn.name(), tsId);
                    generatedKeys.put(dbColumn.name(),tsId);

                }
                else if(dbColumn.isStringFamily()) {
                    log.debug("PK of String family");
                    String tsId = TSID.Factory.getTsid().toString();
                    data.put(dbColumn.name(), tsId);
                    generatedKeys.put(dbColumn.name(),tsId);
                }
                else{
                    throw new GenericDataAccessException("Unable to detect data type family for TSID column.");
                }

                log.debug("Data - {}", data);
            }
        }

        return generatedKeys;
    }


}
