SELECT *
FROM film f
WHERE 1 = 1
{% if params.film_id %}
AND f.film_id = {{ params.film_id }}::integer
{% endif %}
{% if params.rating %}
AND f.rating = {{ params.rating }}::text
{% endif %}
