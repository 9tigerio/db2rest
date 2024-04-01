
Insert into language (language_id,name,last_update) Values (1,'English',SYSDATE);
Insert into language (language_id,name,last_update) Values (2,'Italian',SYSDATE);
Insert into language (language_id,name,last_update) Values (3,'Japanese',SYSDATE);
Insert into language (language_id,name,last_update) Values (4,'Mandarin',SYSDATE);
Insert into language (language_id,name,last_update) Values (5,'French',SYSDATE);
Insert into language (language_id,name,last_update) Values (6,'German',SYSDATE);


Insert into country (country_id,country,last_update) Values (1,'Afghanistan',SYSDATE);
Insert into country (country_id,country,last_update) Values (2,'Algeria',SYSDATE);
Insert into country (country_id,country,last_update) Values (3,'American Samoa',SYSDATE);
Insert into country (country_id,country,last_update) Values (4,'Angola',SYSDATE);
Insert into country (country_id,country,last_update) Values (5,'Anguilla',SYSDATE);
Insert into country (country_id,country,last_update) Values (6,'Argentina',SYSDATE);


Insert into actor (actor_id,first_name,last_name,last_update) Values (1,'PENELOPE','GUINESS',SYSDATE);
Insert into actor (actor_id,first_name,last_name,last_update) Values (2,'NICK','WAHLBERG',SYSDATE);
Insert into actor (actor_id,first_name,last_name,last_update) Values (3,'ED','CHASE',SYSDATE);
Insert into actor (actor_id,first_name,last_name,last_update) Values (4,'JENNIFER','DAVIS',SYSDATE);


Insert into film (film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update) Values (1,'ACADEMY DINOSAUR','A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies',2006,1,NULL,6,0.99,86,20.99,'PG','Deleted Scenes,Behind the Scenes',SYSDATE);
Insert into film (film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update) Values (2,'ACE GOLDFINGER','A Astounding Epistle of a Database Administrator And a Explorer who must Find a Car in Ancient China',2006,1,NULL,3,4.99,48,12.99,'G','Trailers,Deleted Scenes',SYSDATE);
Insert into film (film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update) Values (3,'ADAPTATION HOLES','A Astounding Reflection of a Lumberjack And a Car who must Sink a Lumberjack in A Baloon Factory',2006,1,NULL,7,2.99,50,18.99,'NC-17','Trailers,Deleted Scenes',SYSDATE);
Insert into film (film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update) Values (4,'AFFAIR PREJUDICE','A Fanciful Documentary of a Frisbee And a Lumberjack who must Chase a Monkey in A Shark Tank',2006,1,NULL,5,2.99,117,26.99,'G','Commentaries,Behind the Scenes',SYSDATE);


-- director

Insert into director(director_id,first_name,last_name) Values ('1','Michael','Tam');

Insert into director(director_id,first_name,last_name) Values ('2','Paul','Anderson');

Insert into director(director_id,first_name,last_name) Values ('3','Alex','Chase');

Insert into director(director_id,first_name,last_name) Values ('4','Karol','Davis');



-- users
-- userpreference

Insert into users (auid, username,password) values (1,'admin','pswrd123');

Insert into userprofile (apid, auid, firstname, lastname, email, phone) values (1,1,'Jack', 'Wolf', 'bettestroom@gmail.com','600075764216');

Insert into users (auid,username,password) values (2, 'admin1','pass506');
Insert into userprofile (apid, auid, firstname, lastname, email, phone) values (2, 3, 'Tom', 'Collins', 'tnkc@outlook.com','878511311054');

Insert into users (auid, username,password) values (4,'fox12','45@jgo0');
Insert into userprofile (apid, auid, firstname, lastname, email, phone) values (4,5,'Bill', 'Fonskin', 'bill_1290@gmail.com','450985764216');

Insert into users (auid,username,password) values (6, 'lexus1267','98hnfRT6');
Insert into userprofile (apid, auid, firstname, lastname, email, phone) values (7, 7, 'Ivan', 'Levchenko', 'ivan_new@outlook.com','878511311054');


-- tops


Insert into tops (top_item, color,t_size) values ('sweater',	'red',	'M');

Insert into tops (top_item, color,t_size) values ('shirt',	'blue',	'M');

Insert into tops (top_item, color,t_size) values ('tank_top',	'white',	'S');


-- bottoms

Insert into bottoms (bottom_item, color,b_size) values ('jeans',	'blue'	,'M');

Insert into bottoms (bottom_item, color,b_size) values ('skirt',	'black'	,'S');

Insert into bottoms (bottom_item, color,b_size) values ('shorts',	'red'	,'S');


