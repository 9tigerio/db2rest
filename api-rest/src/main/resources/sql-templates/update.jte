@param String rootTable
@param String rootWhere
@param String columnSets

UPDATE ${rootTable}
SET ${columnSets}
@if(rootWhere != null)
    WHERE ${rootWhere}
@endif
