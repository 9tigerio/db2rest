SET IDENTITY_INSERT category ON;

INSERT INTO employee
    (first_name, last_name)
VALUES ('Ivan', 'Levchenko'),
       ('Roger', 'Federer');
SET IDENTITY_INSERT category OFF;

INSERT INTO tops
    (top_item, color, size)
VALUES ('sweater', 'red', 'M'),
       ('shirt', 'blue', 'M'),
       ('tank_top', 'white', 'S');

INSERT INTO bottoms
    (bottom_item, color, size)
VALUES ('jeans', 'blue', 'M'),
       ('skirt', 'black', 'S'),
       ('shorts', 'red', 'S');

INSERT INTO users
    (auid, username, password, createdate)
VALUES (1, 'admin', 'pswrd123', GETDATE()),
       (2, 'admin1', 'pass506', GETDATE()),
       (4, 'fox12', '45@jgo0', GETDATE()),
       (6, 'lexus1267', '98hnfRT6', GETDATE());

INSERT INTO userprofile
    (apid, auid, firstname, lastname, email, phone)
VALUES (1, 1, 'Jack', 'Wolf', 'bettestroom@gmail.com', '600075764216'),
       (2, 3, 'Tom', 'Collins', 'tnkc@outlook.com', '878511311054'),
       (4, 5, 'Bill', 'Fonskin', 'bill_1290@gmail.com', '450985764216'),
       (7, 7, 'Ivan', 'Levchenko', 'ivan_new@outlook.com', '878511311054');


SET IDENTITY_INSERT "language" ON;
INSERT INTO language
    (language_id, name, last_update)
VALUES (1, 'English', '2006-02-15 05:02:19.000'),
       (2, 'Italian', '2006-02-15 05:02:19.000'),
       (3, 'Japanese', '2006-02-15 05:02:19.000'),
       (6, 'German', '2006-02-15 05:02:19.000');
SET IDENTITY_INSERT "language" OFF;

SET IDENTITY_INSERT actor ON;
INSERT INTO actor
    (actor_id, first_name, last_name, last_update)
VALUES (1, 'PENELOPE', 'GUINESS', '2006-02-15 04:34:33.000'),
       (2, 'NICK', 'WAHLBERG', '2006-02-15 04:34:33.000'),
       (3, 'ED', 'CHASE', '2006-02-15 04:34:33.000'),
       (4, 'JENNIFER', 'DAVIS', '2006-02-15 04:34:33.000');
SET IDENTITY_INSERT actor OFF;


INSERT INTO director
    (director_id, first_name, last_name, last_update)
VALUES (1, 'Michael', 'Tam', '2006-02-15 04:34:33.000'),
       (2, 'Paul', 'Anderson', '2006-02-15 04:34:33.000'),
       (3, 'Alex', 'Chase', '2006-02-15 04:34:33.000'),
       (4, 'Karol', 'Davis', '2006-02-15 04:34:33.000');


SET IDENTITY_INSERT category ON;
INSERT INTO category
    (category_id, name, last_update)
VALUES (1, 'Action', '2006-02-15 04:46:27.000'),
       (2, 'Animation', '2006-02-15 04:46:27.000'),
       (6, 'Documentary', '2006-02-15 04:46:27.000'),
       (4, 'Classics', '2006-02-15 04:46:27.000');
SET IDENTITY_INSERT category OFF;


SET IDENTITY_INSERT film ON;
INSERT INTO film
    (film_id, title, description, release_year, language_id, original_language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features, last_update)
VALUES (1, 'ACADEMY DINOSAUR', 'A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies', '2006', '1', NULL, '6', '0.99', '86', '20.99', 'PG', 'Behind the Scenes', '2006-02-15 05:03:42.000'),
       (2, 'ACE GOLDFINGER', 'A Astounding Epistle of a Database Administrator And a Explorer who must Find a Car in Ancient China', '2006', '1', NULL, '3', '4.99', '48', '12.99', 'G', 'Trailers', '2006-02-15 05:03:42.000'),
       (3, 'ADAPTATION HOLES', 'A Astounding Reflection of a Lumberjack And a Car who must Sink a Lumberjack in A Baloon Factory', '2006', '1', NULL, '7', '2.99', '50', '18.99', 'NC-17', 'Trailers', '2006-02-15 05:03:42.000');
INSERT INTO film
    (film_id, title, description, release_year, language_id, original_language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features, last_update, prequel_film_id)
VALUES (4, 'AFFAIR PREJUDICE', 'A Fanciful Documentary of a Frisbee And a Lumberjack who must Chase a Monkey in A Shark Tank', '2006', '1', NULL, '5', '2.99', '117', '26.99', 'G', 'Commentaries', '2006-02-15 05:03:42.000', 1);

SET IDENTITY_INSERT film OFF;


SET IDENTITY_INSERT country ON;
INSERT INTO country
    (country_id, country, last_update)
VALUES (1, 'Afghanistan', '2006-02-15 04:44:00.000'),
       (2, 'Algeria', '2006-02-15 04:44:00.000'),
       (3, 'American Samoa', '2006-02-15 04:44:00.000'),
       (4, 'Angola', '2006-02-15 04:44:00.000');
SET IDENTITY_INSERT country OFF;

INSERT INTO person
    (name, age)
VALUES ('Stephan', 20),
       ('Thomas', 21),
       ('Alex', 22),
       ('Peter', 23),
       ('Marko', 24),
       ('Kevin', 25),
       ('Anna', 26),
       ('Sofi', 27),
       ('Tamas', 28),
       ('Max', 29);



INSERT INTO review (review_id, message, rating, film_id)
VALUES ('ABC123', 'Awesome movie', 4, 1);

INSERT INTO film_actor
    (actor_id, film_id)
VALUES (1, 1);

