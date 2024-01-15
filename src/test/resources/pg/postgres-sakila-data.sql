
-- language

Insert into language
(language_id,name,last_update)
Values
    ('1','English','2006-02-15 05:02:19.000');

Insert into language
(language_id,name,last_update)
Values
    ('2','Italian','2006-02-15 05:02:19.000');

Insert into language
(language_id,name,last_update)
Values
    ('3','Japanese','2006-02-15 05:02:19.000');

Insert into language
(language_id,name,last_update)
Values
    ('6','German','2006-02-15 05:02:19.000');


-- actor

Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('1','PENELOPE','GUINESS','2006-02-15 04:34:33.000');

Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('2','NICK','WAHLBERG','2006-02-15 04:34:33.000');

Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('3','ED','CHASE','2006-02-15 04:34:33.000');

Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('4','JENNIFER','DAVIS','2006-02-15 04:34:33.000');

-- category

Insert into category
(category_id,name,last_update)
Values
    ('1','Action','2006-02-15 04:46:27.000');

Insert into category
(category_id,name,last_update)
Values
    ('2','Animation','2006-02-15 04:46:27.000');

Insert into category
(category_id,name,last_update)
Values
    ('6','Documentary','2006-02-15 04:46:27.000');

Insert into category
(category_id,name,last_update)
Values
    ('11','Horror','2006-02-15 04:46:27.000');

--

Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('1','ACADEMY DINOSAUR','A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies','2006','1',NULL,'6','0.99','86','20.99','PG',string_to_array('Deleted Scenes,Behind the Scenes',','),'2006-02-15 05:03:42.000');

Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('2','ACE GOLDFINGER','A Astounding Epistle of a Database Administrator And a Explorer who must Find a Car in Ancient China','2006','1',NULL,'3','4.99','48','12.99','G',string_to_array('Trailers,Deleted Scenes',','),'2006-02-15 05:03:42.000');

Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('3','ADAPTATION HOLES','A Astounding Reflection of a Lumberjack And a Car who must Sink a Lumberjack in A Baloon Factory','2006','1',NULL,'7','2.99','50','18.99','NC-17',string_to_array('Trailers,Deleted Scenes',','),'2006-02-15 05:03:42.000');

Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('4','AFFAIR PREJUDICE','A Fanciful Documentary of a Frisbee And a Lumberjack who must Chase a Monkey in A Shark Tank','2006','1',NULL,'5','2.99','117','26.99','G',string_to_array('Commentaries,Behind the Scenes',','),'2006-02-15 05:03:42.000');


-- film_actor

ALTER TABLE film_actor DISABLE TRIGGER ALL;

Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','1','2006-02-15 05:05:03.000');

Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','23','2006-02-15 05:05:03.000');

Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','25','2006-02-15 05:05:03.000');

Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','106','2006-02-15 05:05:03.000');


-- film_category

Insert into film_category
(film_id,category_id,last_update)
Values
    ('1','6','2006-02-15 05:07:09.000');

Insert into film_category
(film_id,category_id,last_update)
Values
    ('2','11','2006-02-15 05:07:09.000');

Insert into film_category
(film_id,category_id,last_update)
Values
    ('3','6','2006-02-15 05:07:09.000');

Insert into film_category
(film_id,category_id,last_update)
Values
    ('4','11','2006-02-15 05:07:09.000');
