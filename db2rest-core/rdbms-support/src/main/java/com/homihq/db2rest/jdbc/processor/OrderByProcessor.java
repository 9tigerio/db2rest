package com.homihq.db2rest.jdbc.processor;

import com.homihq.db2rest.jdbc.config.model.DbSort;
import com.homihq.db2rest.jdbc.dto.ReadContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Order(12)
public class OrderByProcessor implements ReadProcessor {
    @Override
    public void process(ReadContext readContext) {
        //first process order by for root

        if (Objects.nonNull(readContext.getSorts()) && !readContext.getSorts().isEmpty()) {
            List<DbSort> dbSortList = new ArrayList<>();
            for (String sort : readContext.getSorts()) {
                log.debug("SORT - {}", sort);

                String[] sortParts = sort.split(";");

                String sortDirection = "ASC"; //default direction

                if (sortParts.length == 2) {
                    sortDirection = sortParts[1];
                }

                dbSortList.add(new DbSort(readContext.getTableName(),
                        readContext.getRoot().alias(), sortParts[0], sortDirection));
            }

            readContext.setDbSortList(dbSortList);
        }
    }
}
