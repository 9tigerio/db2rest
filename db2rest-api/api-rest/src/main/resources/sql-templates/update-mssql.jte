@param String rootTable
@param String rootWhere
@param String columnSets
@param String rootTableAlias

UPDATE ${rootTableAlias}
SET ${columnSets}
FROM ${rootTable}
@if(rootWhere != null)
    WHERE ${rootWhere}
@endif
