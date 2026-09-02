package com.homihq.db2rest.jdbc.processor;

import com.db2rest.jdbc.dialect.model.DbColumn;
import com.homihq.db2rest.jdbc.dto.ReadContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Order(2)
@Slf4j
public class PrimaryKeyProcessor implements ReadProcessor {

    @Override
    public void process(ReadContext readContext) {
        if (StringUtils.isNoneBlank(readContext.getPrimaryKey())) {

            List<String> columns = readContext.getRoot()
                    .buildPkColumns()
                    .stream()
                    .map(DbColumn::name)
                    .sorted(String.CASE_INSENSITIVE_ORDER).toList();

            List<String> values = Arrays.stream(readContext.getPrimaryKey().split(","))
                    .map(String::trim)
                    .toList();

            if (columns.size() != values.size()) {
                throw new IllegalArgumentException(
                        "Expected " + columns.size() + " primary key values but got " + values.size()
                );
            }

            String pkFilter = IntStream.range(0, columns.size())
                    .mapToObj(i -> columns.get(i) + "==" + values.get(i))
                    .collect(Collectors.joining(";"));

            String existingFilter = readContext.getFilter();

            if (StringUtils.isNoneBlank(existingFilter)) {
                pkFilter = pkFilter + ";" + existingFilter;
            }

            log.debug("Filter - {}", pkFilter);
            readContext.setFilter(pkFilter);
        }
    }
}
