@import com.db2rest.jdbc.dialect.model.DbJoin
@import java.util.List

@param List<DbJoin> joins
@param String rootTable
@param String columns
@param String rootWhere
@param String sorts
@param Integer limit
@param Long offset

SELECT
    ${columns}
FROM
    ${rootTable}
@if(joins != null)
    @for(DbJoin join : joins)
        ${join.render()}
    @endfor
@endif
@if(rootWhere != null)
    WHERE ${rootWhere}
@endif
@if(sorts != null)
    ORDER BY ${sorts}
@endif
@if(limit != null)
    @if(offset != null)
        OFFSET ${offset} ROWS
        FETCH NEXT ${limit} ROWS ONLY
    @else
        FETCH FIRST ${limit} ROWS ONLY
    @endif
@endif
