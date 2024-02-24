package com.homihq.db2rest.rest.create.tsid;

import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.model.DbColumn;
import io.hypersistence.tsid.TSID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TSIDProcessor {

    public void processTsId(Map<String, Object> data, List<DbColumn> pkColumns) {
        log.info("PK Columns - {}", pkColumns);

        for(DbColumn dbColumn : pkColumns) {

            log.info("isGenerated - {}", dbColumn.column().isGenerated());
            log.info("isAutoIncremented - {}", dbColumn.column().isAutoIncremented());

            if(!dbColumn.column().isGenerated() && !dbColumn.column().isAutoIncremented()) {
                //detect type

                log.info("detect type of the TSID column - {}", dbColumn.column().getColumnDataType().getName());

                if(dbColumn.isIntFamily()) {
                    log.info("PK of Int family");
                    data.put(dbColumn.name(), TSID.Factory.getTsid().toLong());

                }
                else if(dbColumn.isStringFamily()) {
                    log.info("PK of String family");
                    data.put(dbColumn.name(), TSID.Factory.getTsid().toString());

                }
                else{
                    throw new GenericDataAccessException("Unable to detect data type family for TSID column.");
                }

                log.info("Data - {}", data);
            }
        }

    }


}
