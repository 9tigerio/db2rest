SELECT
    [# th:each="column : ${columns}"]
    [(${column.name})]
    [/]
FROM
    [(${rootTable.name})]

