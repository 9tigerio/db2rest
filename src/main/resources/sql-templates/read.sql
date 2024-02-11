SELECT
    [# th:each="column : ${columns}"]
    [(${column.name})][/]
FROM
    [(${rootTable.name})]
[# th:if="${rootWhere}"]WHERE
    [(${rootWhere})]
[/]
