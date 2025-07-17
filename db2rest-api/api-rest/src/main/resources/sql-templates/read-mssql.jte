@import com.homihq.db2rest.jdbc.config.model.DbJoin
@import java.util.List

@param List<DbJoin> joins
@param String rootTable
@param String columns
@param String rootWhere
@param String sorts
@param Integer limit
@param Long offset

@if(sorts != null)
    SELECT
        ${columns}
    FROM ${rootTable}
    @if(joins != null)
        @for(DbJoin join : joins)
            ${join.render()}
        @endfor
    @endif
    @if(rootWhere != null)
        WHERE ${rootWhere}
    @endif
    ORDER BY ${sorts}
    @if(limit != null)
        OFFSET ${(offset == null ? 0 : offset)} ROWS FETCH NEXT ${limit} ROWS ONLY
    @endif
@elseif(limit != null)
    @if(offset != null)
        SELECT T.* FROM
        (
            SELECT
                ${columns},
                ROW_NUMBER() OVER(ORDER BY (SELECT 1)) AS rowIndex
            FROM ${rootTable}
            @if(joins != null)
                @for(DbJoin join : joins)
                    ${join.render()}
                @endfor
            @endif
            @if(rootWhere != null)
                WHERE ${rootWhere}
            @endif
        ) AS T
        WHERE rowIndex > ${offset} AND rowIndex <= ${offset + limit}
    @else
        SELECT TOP ${limit} ${columns}
        FROM ${rootTable}
        @if(joins != null)
            @for(DbJoin join : joins)
                ${join.render()}
            @endfor
        @endif
        @if(rootWhere != null)
            WHERE ${rootWhere}
        @endif
    @endif
@else
    SELECT
        ${columns}
    FROM ${rootTable}
    @if(joins != null)
        @for(DbJoin join : joins)
            ${join.render()}
        @endfor
    @endif
    @if(rootWhere != null)
        WHERE ${rootWhere}
    @endif
@endif
