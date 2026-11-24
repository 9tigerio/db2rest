-- db2rest:operation=delete

  DELETE FROM actor WHERE actor_id = {{ content.actor_id }}
