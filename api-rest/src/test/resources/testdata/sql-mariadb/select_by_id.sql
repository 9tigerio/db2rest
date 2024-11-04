SELECT *
FROM sakila.film f
WHERE 1 = 1
  AND f.film_id =
    {% if params.film_id %}
        {{ params.film_id }}
    {% elif paths.film_id %}
        {{ paths.film_id }}
    {% elif headers.film_id %}
        {{ headers.film_id }}
    {% endif %}
