DROP SCHEMA IF EXISTS stg CASCADE;
CREATE SCHEMA stg;

CREATE TABLE stg."FilmData" (
    film_id integer PRIMARY KEY,
    title character varying(100) NOT NULL
);

INSERT INTO stg."FilmData" (film_id, title)
VALUES (916, 'Case-sensitive table names');
