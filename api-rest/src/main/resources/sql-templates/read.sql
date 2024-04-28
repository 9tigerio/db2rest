SELECT
    [(${columns})]
FROM
    [(${rootTable})]
[# th:if="${joins}"][# th:each="join : ${joins}"][(${join.render()})][/][/]
[# th:if="${rootWhere}"]WHERE
    [(${rootWhere})][/]
[# th:if="${sorts}"]ORDER BY [(${sorts})] [/]
[# th:if="${limit}"]LIMIT [(${limit})] [# th:if="${offset}"]OFFSET [(${offset})][/] [/]
