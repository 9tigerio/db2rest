[# th:if="${sorts}"]
    SELECT
        [(${columns})]
    FROM
    [(${rootTable})]
    [# th:if="${joins}"]
        [# th:each ="join:${joins}"][(${join.render()})][/]
    [/]
    [# th:if="${rootWhere}"]
        WHERE [(${rootWhere})]
    [/]
    ORDER BY [(${sorts})]
    [# th:if="${limit}"]
        OFFSET [(${offset ?: 0})] ROWS FETCH NEXT [(${limit})] ROWS ONLY
    [/]
[/]
[# th:unless="${sorts}"]
    [# th:if="${limit}"]
        [# th:if="${offset}"]
            SELECT T.* FROM
                (
                    SELECT
                        [(${columns})],
                        ROW_NUMBER() OVER(ORDER BY (SELECT 1)) AS rowIndex
                    FROM [(${rootTable})]
                    [# th:if="${joins}"]
                        [# th:each="join : ${joins}"][(${join.render()})][/]
                    [/]
                    [# th:if="${rootWhere}"]
                        WHERE [(${rootWhere})]
                    [/]
                ) AS T
            WHERE rowIndex > [(${offset})] AND rowIndex <= [(${offset + limit})]
        [/]
        [# th:unless="${offset}"]
            SELECT TOP [(${limit})] [(${columns})]
            FROM [(${rootTable})]
            [# th:if="${joins}"]
                [# th:each="join : ${joins}"][(${join.render()})][/]
            [/]
            [# th:if="${rootWhere}"]
                WHERE [(${rootWhere})]
            [/]
        [/]
    [/]
[/]
