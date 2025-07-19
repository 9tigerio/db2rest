SELECT f.title, a.first_name, a.last_name
FROM film f
INNER JOIN film_actor fa ON f.film_id = fa.film_id
INNER JOIN actor a ON fa.actor_id = a.actor_id
WHERE 1 = 1
  {% if params.actor_name %}
    AND a.first_name LIKE '%{{ params.actor_name }}%'
  {% endif %}
  {% if params.film_title %}
    AND f.title LIKE '%{{ params.film_title }}%'
  {% endif %}