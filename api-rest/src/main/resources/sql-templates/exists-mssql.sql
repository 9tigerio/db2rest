SELECT TOP 1 1
FROM
[(${rootTable})]
[# th:if="${joins}"]
    [# th:each="join : ${joins}"][(${join.render()})][/]
[/]
[# th:if="${rootWhere}"]
    WHERE [(${rootWhere})]
[/]
