@param String rootTable
@param String rootWhere

SELECT
    COUNT(*)
FROM
    ${rootTable}
@if(rootWhere != null)
    WHERE ${rootWhere}
@endif
