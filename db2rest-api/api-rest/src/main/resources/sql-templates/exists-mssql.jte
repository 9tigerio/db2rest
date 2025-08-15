@import com.db2rest.jdbc.dialect.model.DbJoin
@import java.util.List

@param String rootTable
@param String rootWhere
@param List<DbJoin> joins

SELECT TOP 1 1
FROM ${rootTable}
@if(joins != null)
    @for(DbJoin join : joins)
        ${join.render()}
    @endfor
@endif
@if(rootWhere != null)
    WHERE ${rootWhere}
@endif
