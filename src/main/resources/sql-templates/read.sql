SELECT
    [(${columns})]
FROM
    [(${rootTable})]
[# th:if="${rootWhere}"]WHERE
    [(${rootWhere})]
[/]
[# th:if="${limit}"]LIMIT [(${limit})][/] [# th:if="${offset}"]LIMIT [(${offset})][/]
