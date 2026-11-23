-- Adds one actor. The operation is detected from the INSERT keyword.
INSERT INTO actor (actor_id, first_name, last_name)
VALUES ({{ content.actor_id }}, {{ content.first_name }}, {{ content.last_name }})
