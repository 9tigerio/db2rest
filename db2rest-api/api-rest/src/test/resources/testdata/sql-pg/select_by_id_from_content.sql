SELECT *
FROM film f
WHERE 1 = 1
  AND f.film_id = {{ content.film_id }}
