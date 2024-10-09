@import com.homihq.db2rest.jdbc.config.model.DbJoin
@import java.util.List

@param String rootTable
@param String rootWhere
@param List<DbJoin> joins

SELECT
    1
FROM ${rootTable}
@if(joins != null)
    @for(DbJoin join : joins)
        ${join.render()}
    @endfor
@endif
@if(rootWhere != null)
    WHERE ${rootWhere}
@endif
LIMIT 1
