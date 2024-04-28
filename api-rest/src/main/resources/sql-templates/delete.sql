DELETE FROM [(${rootTable})]
[# th:if="${rootWhere}"]WHERE
[(${rootWhere})]
[/]
