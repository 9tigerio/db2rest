SELECT * FROM film f
WHERE f.film_id = {{ params.film_id | is_required }}::integer
