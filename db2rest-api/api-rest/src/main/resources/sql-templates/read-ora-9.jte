@import com.homihq.db2rest.jdbc.config.model.DbJoin
@import java.util.List

@param List<DbJoin> joins
@param String rootTable
@param String columns
@param String rootWhere
@param String sorts
@param Integer limit
@param Long offset

@if(limit != null)
    @if(offset != null)
        SELECT T.* FROM
        (
            SELECT T.*, rownum as rowIndex FROM
            (
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
            ) T
        ) T
        WHERE rowIndex > ${offset} AND rowIndex <= ${offset + limit}
    @else
        SELECT * FROM (
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
        )
        WHERE ROWNUM <= ${limit}
    @endif
@else
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
@endif
