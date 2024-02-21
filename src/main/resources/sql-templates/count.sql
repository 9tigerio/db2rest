SELECT
    count(*)
FROM
    [(${rootTable})]
[# th:if="${rootWhere}"]WHERE
    [(${rootWhere})][/]
