SELECT
    [(${columns})]
FROM
    [(${rootTable})]
[# th:if="${rootWhere}"]WHERE
    [(${rootWhere})]
[/]
[# th:if="${joins}"]
    [# th:each="join : ${joins}"]
    [(${join.render()})]
    [/]
[/]
[# th:if="${limit}"]LIMIT [(${limit})][/] [# th:if="${offset}"]LIMIT [(${offset})][/]
