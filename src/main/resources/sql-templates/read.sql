SELECT
    [(${columns})]
FROM
    [(${rootTable})]
[# th:if="${joins}"][# th:each="join : ${joins}"][(${join.render()})][/][/]
[# th:if="${limit}"]LIMIT [(${limit})][/] [# th:if="${offset}"]LIMIT [(${offset})][/]
[# th:if="${rootWhere}"]WHERE
[(${rootWhere})]
[/]
