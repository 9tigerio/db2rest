SELECT *
FROM film f
WHERE 1 = 1
  {% if params.film_id %}
    AND f.film_id = {{ params.film_id }}
  {% endif %}
  {% if params.title %}
    AND f.title LIKE '%{{ params.title }}%'
  {% endif %}
  {% if params.rating %}
    AND f.rating = '{{ params.rating }}'
  {% endif %}