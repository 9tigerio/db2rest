SELECT
    {% if params.film_id and params.language_id %}
        f.film_id, f.title, l.name as language_name
    {% else %}
        *
    {% endif %}
FROM film f

{% if params.film_id and params.language_id %}
JOIN
    language l
ON f.language_id = l.language_id
{% endif %}

{% if params.film_id %}
WHERE f.film_id = {{ params.film_id | is_required }}::integer
{% endif %}
