SELECT
    [(${columns})]
FROM
    [(${rootTable})]
[# th:if="${joins}"][# th:each="join : ${joins}"][(${join.render()})][/][/]
[# th:if="${rootWhere}"]WHERE
    [(${rootWhere})][/]
[# th:if="${sorts}"]ORDER BY [(${sorts})] [/]
[# th:if="${limit}"]
    [# th:if="${offset}"]
OFFSET [(${offset})] ROWS
FETCH NEXT [(${limit})] ROWS ONLY
    [/]
    [# th:unless="${offset}"]
FETCH FIRST [(${limit})] ROWS ONLY
    [/]
[/]

