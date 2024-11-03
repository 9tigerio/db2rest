@param String rootTable
@param String rootWhere
@param String rootTableAlias

DELETE FROM ${rootTableAlias}
FROM ${rootTable}

@if(rootWhere != null)
    WHERE ${rootWhere}
@endif
