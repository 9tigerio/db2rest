-- Sample data for SQLite Sakila Database

-- tops
INSERT INTO tops (top_item, color, size) VALUES ('sweater', 'red', 'M');
INSERT INTO tops (top_item, color, size) VALUES ('shirt', 'blue', 'M');
INSERT INTO tops (top_item, color, size) VALUES ('tank_top', 'white', 'S');

-- bottoms
INSERT INTO bottoms (bottom_item, color, size) VALUES ('jeans', 'blue', 'M');
INSERT INTO bottoms (bottom_item, color, size) VALUES ('skirt', 'black', 'S');
INSERT INTO bottoms (bottom_item, color, size) VALUES ('shorts', 'red', 'S');

-- users
INSERT INTO users (auid, username, password, createdate, isActive) 
VALUES (1, 'admin', 'pswrd123', datetime('now'), 1);

-- userprofile
INSERT INTO userprofile (apid, auid, firstname, lastname, email, phone)
VALUES (1, 1, 'admin', 'admin', 'admin@email.com', '1234567890');

-- language
INSERT INTO language (language_id, name) VALUES (1, 'English');
INSERT INTO language (language_id, name) VALUES (2, 'Italian');
INSERT INTO language (language_id, name) VALUES (3, 'Japanese');
INSERT INTO language (language_id, name) VALUES (4, 'Mandarin');
INSERT INTO language (language_id, name) VALUES (5, 'French');
INSERT INTO language (language_id, name) VALUES (6, 'German');

-- category
INSERT INTO category (category_id, name) VALUES (1, 'Action');
INSERT INTO category (category_id, name) VALUES (2, 'Animation');
INSERT INTO category (category_id, name) VALUES (3, 'Children');
INSERT INTO category (category_id, name) VALUES (4, 'Classics');
INSERT INTO category (category_id, name) VALUES (5, 'Comedy');
INSERT INTO category (category_id, name) VALUES (6, 'Documentary');
INSERT INTO category (category_id, name) VALUES (7, 'Drama');
INSERT INTO category (category_id, name) VALUES (8, 'Family');
INSERT INTO category (category_id, name) VALUES (9, 'Foreign');
INSERT INTO category (category_id, name) VALUES (10, 'Games');
INSERT INTO category (category_id, name) VALUES (11, 'Horror');
INSERT INTO category (category_id, name) VALUES (12, 'Music');
INSERT INTO category (category_id, name) VALUES (13, 'New');
INSERT INTO category (category_id, name) VALUES (14, 'Sci-Fi');
INSERT INTO category (category_id, name) VALUES (15, 'Sports');
INSERT INTO category (category_id, name) VALUES (16, 'Travel');

-- actor
INSERT INTO actor (actor_id, first_name, last_name) VALUES (1, 'PENELOPE', 'GUINESS');
INSERT INTO actor (actor_id, first_name, last_name) VALUES (2, 'NICK', 'WAHLBERG');
INSERT INTO actor (actor_id, first_name, last_name) VALUES (3, 'ED', 'CHASE');
INSERT INTO actor (actor_id, first_name, last_name) VALUES (4, 'JENNIFER', 'DAVIS');
INSERT INTO actor (actor_id, first_name, last_name) VALUES (5, 'JOHNNY', 'LOLLOBRIGIDA');
INSERT INTO actor (actor_id, first_name, last_name) VALUES (6, 'BETTE', 'NICHOLSON');
INSERT INTO actor (actor_id, first_name, last_name) VALUES (7, 'GRACE', 'MOSTEL');
INSERT INTO actor (actor_id, first_name, last_name) VALUES (8, 'MATTHEW', 'JOHANSSON');
INSERT INTO actor (actor_id, first_name, last_name) VALUES (9, 'JOE', 'SWANK');
INSERT INTO actor (actor_id, first_name, last_name) VALUES (10, 'CHRISTIAN', 'GABLE');

-- film
INSERT INTO film (film_id, title, description, release_year, language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features)
VALUES (1, 'ACADEMY DINOSAUR', 'A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies', 2006, 1, 6, 0.99, 86, 20.99, 'PG', 'Deleted Scenes,Behind the Scenes');

INSERT INTO film (film_id, title, description, release_year, language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features)
VALUES (2, 'ACE GOLDFINGER', 'A Astounding Epistle of a Database Administrator And a Explorer who must Find a Car in Ancient China', 2006, 1, 3, 4.99, 48, 12.99, 'G', 'Trailers,Deleted Scenes');

INSERT INTO film (film_id, title, description, release_year, language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features)
VALUES (3, 'ADAPTATION HOLES', 'A Astounding Reflection of a Lumberjack And a Car who must Sink a Lumberjack in A Baloon Factory', 2006, 1, 7, 2.99, 50, 18.99, 'NC-17', 'Trailers,Deleted Scenes');

INSERT INTO film (film_id, title, description, release_year, language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features)
VALUES (4, 'AFFAIR PREJUDICE', 'A Fanciful Documentary of a Frisbee And a Lumberjack who must Chase a Monkey in A Shark Tank', 2006, 1, 5, 2.99, 117, 26.99, 'G', 'Commentaries,Behind the Scenes');

INSERT INTO film (film_id, title, description, release_year, language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features)
VALUES (5, 'AFRICAN EGG', 'A Fast-Paced Documentary of a Pastry Chef And a Dentist who must Pursue a Forensic Psychologist in The Gulf of Mexico', 2006, 1, 6, 2.99, 130, 22.99, 'G', null);

-- film_actor relationships
INSERT INTO film_actor (actor_id, film_id) VALUES (1, 1);
INSERT INTO film_actor (actor_id, film_id) VALUES (1, 2);
INSERT INTO film_actor (actor_id, film_id) VALUES (2, 1);
INSERT INTO film_actor (actor_id, film_id) VALUES (2, 3);
INSERT INTO film_actor (actor_id, film_id) VALUES (3, 2);
INSERT INTO film_actor (actor_id, film_id) VALUES (3, 4);

-- film_category relationships
INSERT INTO film_category (film_id, category_id) VALUES (1, 6);
INSERT INTO film_category (film_id, category_id) VALUES (2, 11);
INSERT INTO film_category (film_id, category_id) VALUES (3, 6);
INSERT INTO film_category (film_id, category_id) VALUES (4, 11);
INSERT INTO film_category (film_id, category_id) VALUES (5, 8);

-- country
INSERT INTO country (country_id, country) VALUES (1, 'Afghanistan');
INSERT INTO country (country_id, country) VALUES (2, 'Algeria');
INSERT INTO country (country_id, country) VALUES (3, 'American Samoa');
INSERT INTO country (country_id, country) VALUES (4, 'Angola');
INSERT INTO country (country_id, country) VALUES (5, 'Anguilla');

-- person
INSERT INTO person (person_id, name, age) VALUES (1, 'John Doe', 30);
INSERT INTO person (person_id, name, age) VALUES (2, 'Jane Smith', 25);
INSERT INTO person (person_id, name, age) VALUES (3, 'Bob Johnson', 35);

-- employee
INSERT INTO employee (emp_id, first_name, last_name, is_active) VALUES (1, 'Alice', 'Wonder', 1);
INSERT INTO employee (emp_id, first_name, last_name, is_active) VALUES (2, 'Bob', 'Builder', 1);
INSERT INTO employee (emp_id, first_name, last_name, is_active) VALUES (3, 'Charlie', 'Brown', 0);

-- department
INSERT INTO department (dept_id, name, is_active) VALUES (1, 'Engineering', 1);
INSERT INTO department (dept_id, name, is_active) VALUES (2, 'Marketing', 1);
INSERT INTO department (dept_id, name, is_active) VALUES (3, 'Sales', 1);