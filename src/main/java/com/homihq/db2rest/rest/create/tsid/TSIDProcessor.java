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
        for(DbColumn dbColumn : pkColumns) {
            if(!dbColumn.column().isGenerated() && !dbColumn.column().isAutoIncremented()) {
                //detect type
                if(dbColumn.isIntFamily()) {
                    data.put(dbColumn.name(), TSID.Factory.getTsid().toLong());
                }
                else if(dbColumn.isStringFamily()) {
                    data.put(dbColumn.name(), TSID.Factory.getTsid().toString());
                }
                else{
                    throw new GenericDataAccessException("Unable to detect data type family for TSID column.");
                }
            }
        }

    }


}
