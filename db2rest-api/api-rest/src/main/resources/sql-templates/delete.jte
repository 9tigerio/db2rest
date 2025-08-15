@param String rootTable
@param String rootWhere

DELETE FROM
    ${rootTable}
@if(rootWhere != null)
    WHERE ${rootWhere}
@endif
