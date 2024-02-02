package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.mybatis.DB2RestRenderingStrategy;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.insert.BatchInsertDSL;
import org.mybatis.dynamic.sql.insert.GeneralInsertDSL;
import org.mybatis.dynamic.sql.insert.render.BatchInsert;
import org.mybatis.dynamic.sql.insert.render.GeneralInsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.dao.DataAccessException;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mybatis.dynamic.sql.insert.BatchInsertDSL.insert;
import static org.mybatis.dynamic.sql.insert.GeneralInsertDSL.insertInto;


@Service
@Slf4j
@RequiredArgsConstructor
public class CreateService {

    private final Db2RestConfigProperties db2RestConfigProperties;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DB2RestRenderingStrategy db2RestRenderingStrategy = new DB2RestRenderingStrategy();
	private final String DEFAULT_GENERATED_KEY_NAME = "GENERATED_KEY";

	@Transactional
	public Pair<Integer, Object> save(String schemaName, String tableName, Map<String, Object> data, String tsid, String tsidType) {
		//db2RestConfigProperties.verifySchema(schemaName);

        processTSID(data, tsid, tsidType);

        SqlTable table = SqlTable.of(tableName);
        GeneralInsertDSL generalInsertDSL = insertInto(table);

        for(String key : data.keySet()) {
            generalInsertDSL.set(table.column(key)).toValue(data.get(key));
        }

        GeneralInsertStatementProvider insertStatement = generalInsertDSL.build().render(RenderingStrategies.SPRING_NAMED_PARAMETER);

        log.debug("SQL - {}", insertStatement.getInsertStatement());
        log.debug("SQL - row - {}", insertStatement.getParameters());

		int row;

		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			row = namedParameterJdbcTemplate.update(insertStatement.getInsertStatement(),
					new MapSqlParameterSource(insertStatement.getParameters()),
					keyHolder
			);
		} catch (DataAccessException e) {
			throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
		}

		String primaryColName = getPrimaryKeyColName(tableName);
		Object generated_key = extractGeneratedKeys(List.of(data), keyHolder.getKeyList(), tsid, primaryColName).getFirst();

		log.debug("Inserted - {} row(s), generated key - {}", row, generated_key);

		return Pair.of(row, Objects.requireNonNull(generated_key));
	}

    private void processTSID(Map<String, Object> data, String tsid, String tsidType) {
        //1. check if tsid column is specified if yes go ahead add or update it with generated TSID value

        if(StringUtils.isNotBlank(tsid)) {
            data.put(tsid, getTSIDValue(tsidType));
        }

        // adding to data will ensure its added in select statement

    }

    private Object getTSIDValue(String tsidType) {
        if(StringUtils.equalsAnyIgnoreCase(tsidType, "number", "string")) {
            return
            StringUtils.equalsIgnoreCase(tsidType, "number") ? TSID.Factory.getTsid().toLong() :
                    TSID.Factory.getTsid().toString();


        }

        throw new GenericDataAccessException("Invalid TSID data type.");

    }

	@Transactional
	public Pair<int[], List<Object>> saveBulk(String schemaName, String tableName, List<Map<String, Object>> dataList, String tsid, String tsidType) {
		if (Objects.isNull(dataList) || dataList.isEmpty()) throw new RuntimeException("No data provided");

        for(Map<String, Object> data : dataList)
            processTSID(data, tsid, tsidType);

        SqlTable table = SqlTable.of(tableName);

        BatchInsertDSL<Map<String, Object>> batchInsertDSL = insert(dataList)
                .into(table);

		Map<String, Object> item = dataList.getFirst();

        for(String key : item.keySet()) {
            batchInsertDSL.map(table.column(key)).toProperty(key);
        }

        BatchInsert<Map<String,Object>> batchInsert =
                batchInsertDSL
                .build()
                .render(db2RestRenderingStrategy);

        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(dataList.toArray());


        log.debug("SQL -> {}", batchInsert.getInsertStatementSQL());
        log.debug("batch -> {}", batchInsert.getRecords());

        int[] updateCounts;
		KeyHolder keyHolder = new GeneratedKeyHolder();

		String primaryColName = getPrimaryKeyColName(tableName);

		try {
			updateCounts = namedParameterJdbcTemplate.batchUpdate(batchInsert.getInsertStatementSQL(), batch, keyHolder);
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
		}

        log.debug("Update counts - {}", updateCounts.length);

		List<Object> generatedKeys = extractGeneratedKeys(dataList, keyHolder.getKeyList(), tsid, primaryColName);

		return Pair.of(updateCounts, generatedKeys);
	}

	private List<Object> extractGeneratedKeys(final List<Map<String, Object>> dataList, final List<Map<String, Object>> keyList, final String tsid, final String primaryColName) {
		String keyName;
		List<Map<String, Object>> keySource;

		if (StringUtils.isNotBlank(tsid)) {
			keyName = tsid;
			keySource = dataList;
		} else {
			keySource = keyList;

			boolean usingDefaultGeneratedKey = keyList.stream()
					.anyMatch(map -> map.containsKey(DEFAULT_GENERATED_KEY_NAME));

			keyName = usingDefaultGeneratedKey ? DEFAULT_GENERATED_KEY_NAME : primaryColName;
		}

		return keySource.stream()
				.map(data -> Optional.ofNullable(data.get(keyName)))
				.flatMap(Optional::stream)
				.collect(Collectors.toList());
	}

	private String getPrimaryKeyColName(final String tableName) {
		DataSource dataSource = namedParameterJdbcTemplate.getJdbcTemplate().getDataSource();

		if (dataSource == null) {
			throw new IllegalStateException("DataSource is null");
		}

		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			ResultSet rs = databaseMetaData.getPrimaryKeys(null, null, tableName);

			if (rs.next()) {
				return rs.getString("COLUMN_NAME");
			}
		} catch (SQLException e) {
			throw new GenericDataAccessException("Error retrieving primary key column name for table " + tableName);
		}

		return "";
	}
}
