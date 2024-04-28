[# th:if="${limit}"]

    [# th:if="${offset}"]
SELECT T.* FROM (
    SELECT T.*, rownum as rowIndex FROM
    (
        SELECT
            [(${columns})]
        FROM
            [(${rootTable})]
        [# th:if="${joins}"][# th:each="join : ${joins}"][(${join.render()})][/][/]
        [# th:if="${rootWhere}"]WHERE
            [(${rootWhere})][/]
        [# th:if="${sorts}"]ORDER BY [(${sorts})] [/]
    ) T) T
WHERE rowIndex > [(${offset})] AND rowIndex <= [(${offset + limit})]
    [/]

    [# th:unless="${offset}"]

SELECT * FROM (
    SELECT
        [(${columns})]
    FROM
        [(${rootTable})]
    [# th:if="${joins}"][# th:each="join : ${joins}"][(${join.render()})][/][/]
    [# th:if="${rootWhere}"]WHERE
        [(${rootWhere})][/]
    [# th:if="${sorts}"]ORDER BY [(${sorts})] [/]
)
WHERE ROWNUM <= [(${limit})]
    [/]

[/]

[# th:unless="${limit}"]
SELECT
    [(${columns})]
FROM
    [(${rootTable})]
[# th:if="${joins}"][# th:each="join : ${joins}"][(${join.render()})][/][/]
[# th:if="${rootWhere}"]WHERE
    [(${rootWhere})][/]
[# th:if="${sorts}"]ORDER BY [(${sorts})] [/]

[/]
