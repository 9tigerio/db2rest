
-- tops


Insert into tops
(top_item, color,size)
values
    ('sweater',	'red',	'M');

Insert into tops
(top_item, color,size)
values
    ('shirt',	'blue',	'M');

Insert into tops
(top_item, color,size)
values
    ('tank_top',	'white',	'S');


-- bottoms

Insert into bottoms
(bottom_item, color,size)
values
    ('jeans',	'blue'	,'M');

Insert into bottoms
(bottom_item, color,size)
values
    ('skirt',	'black'	,'S');

Insert into bottoms
(bottom_item, color,size)
values
    ('shorts',	'red'	,'S');




-- users
-- userpreference

Insert into users
(auid, username,password, createdate, isActive)
values
    (1,'admin','pswrd123', curdate(), 1);

Insert into userprofile
(apid, auid, firstname, lastname, email, phone)
values
    (1,1,'Jack', 'Wolf', 'bettestroom@gmail.com','600075764216');

Insert into users
(auid,username,password, createdate, isActive)
values
    (2, 'admin1','pass506', curdate(), 1);

Insert into userprofile
(apid, auid, firstname, lastname, email, phone)
values
    (2, 3, 'Tom', 'Collins', 'tnkc@outlook.com','878511311054');

Insert into users
(auid, username,password, createdate, isActive)
values
    (4,'fox12','45@jgo0', curdate(), 1);

Insert into userprofile
(apid, auid, firstname, lastname, email, phone)
values
    (4,5,'Bill', 'Fonskin', 'bill_1290@gmail.com','450985764216');

Insert into users
(auid,username,password, createdate, isActive)
values
    (6, 'lexus1267','98hnfRT6', curdate(), 1);

Insert into userprofile
(apid, auid, firstname, lastname, email, phone)
values
    (7, 7, 'Ivan', 'Levchenko', 'ivan_new@outlook.com','878511311054');


Insert into employee (emp_id, first_name, last_name, create_date, is_active) values (1, 'Ivan', 'Levchenko', curdate(), 1);
Insert into employee (emp_id, first_name, last_name, create_date, is_active) values (2, 'Roger', 'Federer', curdate(), 1);


-- language

Insert into language
(`language_id`,`name`,`last_update`)
Values
    ('1','English','2006-02-15 05:02:19.000');

Insert into language
(`language_id`,`name`,`last_update`)
Values
    ('2','Italian','2006-02-15 05:02:19.000');

Insert into language
(`language_id`,`name`,`last_update`)
Values
    ('3','Japanese','2006-02-15 05:02:19.000');

Insert into language
(`language_id`,`name`,`last_update`)
Values
    ('6','German','2006-02-15 05:02:19.000');



-- actor

Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('1','PENELOPE','GUINESS','2006-02-15 04:34:33.000');

Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('2','NICK','WAHLBERG','2006-02-15 04:34:33.000');

Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('3','ED','CHASE','2006-02-15 04:34:33.000');

Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('4','JENNIFER','DAVIS','2006-02-15 04:34:33.000');


-- director

Insert into director
(director_id,first_name,last_name,last_update)
Values
    ('1','Michael','Tam','2006-02-15 04:34:33.000');

Insert into director
(director_id,first_name,last_name,last_update)
Values
    ('2','Paul','Anderson','2006-02-15 04:34:33.000');

Insert into director
(director_id,first_name,last_name,last_update)
Values
    ('3','Alex','Chase','2006-02-15 04:34:33.000');

Insert into director
(director_id,first_name,last_name,last_update)
Values
    ('4','Karol','Davis','2006-02-15 04:34:33.000');


-- category

Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('1','Action','2006-02-15 04:46:27.000');

Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('2','Animation','2006-02-15 04:46:27.000');

Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('3','Children','2006-02-15 04:46:27.000');

Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('4','Classics','2006-02-15 04:46:27.000');


-- film

Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('1','ACADEMY DINOSAUR','A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies','2006','1',NULL,'6','0.99','86','20.99','PG','Deleted Scenes,Behind the Scenes','2006-02-15 05:03:42.000');

Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('2','ACE GOLDFINGER','A Astounding Epistle of a Database Administrator And a Explorer who must Find a Car in Ancient China','2006','1',NULL,'3','4.99','48','12.99','G','Trailers,Deleted Scenes','2006-02-15 05:03:42.000');

Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('3','ADAPTATION HOLES','A Astounding Reflection of a Lumberjack And a Car who must Sink a Lumberjack in A Baloon Factory','2006','1',NULL,'7','2.99','50','18.99','NC-17','Trailers,Deleted Scenes','2006-02-15 05:03:42.000');

Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update` , `prequel_film_id`)
Values
    ('4','AFFAIR PREJUDICE','A Fanciful Documentary of a Frisbee And a Lumberjack who must Chase a Monkey in A Shark Tank','2006','1',NULL,'5','2.99','117','26.99','G','Commentaries,Behind the Scenes','2006-02-15 05:03:42.000',1);



-- country

Insert into country
 (`country_id`,`country`,`last_update`)
Values
('1','Afghanistan','2006-02-15 04:44:00.000');

Insert into country
 (`country_id`,`country`,`last_update`)
Values
('2','Algeria','2006-02-15 04:44:00.000');

Insert into country
 (`country_id`,`country`,`last_update`)
Values
('3','American Samoa','2006-02-15 04:44:00.000');

Insert into country
 (`country_id`,`country`,`last_update`)
Values
('4','Angola','2006-02-15 04:44:00.000');


--review
INSERT INTO review (review_id , message, rating ,film_id) VALUES ('ABC123','Awesome movie', 4, 1);


-- film_actor

INSERT INTO film_actor
(actor_id, film_id)
VALUES(1, 1);
