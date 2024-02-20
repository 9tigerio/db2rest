SELECT
    [(${columns})]
FROM
    [(${rootTable})]
[# th:if="${rootWhere}"]WHERE
    [(${rootWhere})][/]
