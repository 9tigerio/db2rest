DELETE [(${rootTableAlias})]
FROM [(${rootTable})]
[# th:if="${rootWhere}"]
    WHERE
[(${rootWhere})][/]
