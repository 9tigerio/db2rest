@param String rootTable
@param String rootWhere
@param String columns

SELECT
    ${columns}
FROM
    ${rootTable}
@if(rootWhere != null)
    WHERE ${rootWhere}
@endif
