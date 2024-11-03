SELECT *
FROM film f
WHERE 1 = 1
  AND f.film_id =
    {% if params.film_id %}
        {{ params.film_id }}::integer
    {% elif paths.film_id %}
        {{ paths.film_id }}::integer
    {% elif headers.film_id %}
        {{ headers.film_id }}::integer
    {% endif %}
