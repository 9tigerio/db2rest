
-- Sakila Sample Database Schema
-- Version 0.8

-- Copyright (c) 2006, MySQL AB
-- All rights reserved.

-- Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

--  * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
--  * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
--  * Neither the name of MySQL AB nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS sakila;
CREATE SCHEMA sakila;
USE sakila;

--
-- Table structure for table `actor`
--

CREATE TABLE actor (
  actor_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(45) NOT NULL,
  last_name VARCHAR(45) NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY  (actor_id),
  KEY idx_actor_last_name (last_name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `address`
--

CREATE TABLE address (
  address_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  address VARCHAR(50) NOT NULL,
  address2 VARCHAR(50) DEFAULT NULL,
  district VARCHAR(20) NOT NULL,
  city_id INT UNSIGNED NOT NULL,
  postal_code VARCHAR(10) DEFAULT NULL,
  phone VARCHAR(20) NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY  (address_id),
  KEY idx_fk_city_id (city_id),
  CONSTRAINT `fk_address_city` FOREIGN KEY (city_id) REFERENCES city (city_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `category`
--

CREATE TABLE category (
  category_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(25) NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY  (category_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `city`
--

CREATE TABLE city (
  city_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  city VARCHAR(50) NOT NULL,
  country_id INT UNSIGNED NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY  (city_id),
  KEY idx_fk_country_id (country_id),
  CONSTRAINT `fk_city_country` FOREIGN KEY (country_id) REFERENCES country (country_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `country`
--

CREATE TABLE country (
  country_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  country VARCHAR(50) NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY  (country_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `customer`
--

CREATE TABLE customer (
  customer_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  store_id INT UNSIGNED NOT NULL,
  first_name VARCHAR(45) NOT NULL,
  last_name VARCHAR(45) NOT NULL,
  email VARCHAR(50) DEFAULT NULL,
  address_id INT UNSIGNED NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  create_date DATETIME NOT NULL,
  last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY  (customer_id),
  KEY idx_fk_store_id (store_id),
  KEY idx_fk_address_id (address_id),
  KEY idx_last_name (last_name),
  CONSTRAINT fk_customer_address FOREIGN KEY (address_id) REFERENCES address (address_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_customer_store FOREIGN KEY (store_id) REFERENCES store (store_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `film`
--

CREATE TABLE film (
  film_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  description TEXT DEFAULT NULL,
  release_year YEAR DEFAULT NULL,
  language_id INT UNSIGNED NOT NULL,
  original_language_id INT UNSIGNED DEFAULT NULL,
  rental_duration TINYINT UNSIGNED NOT NULL DEFAULT 3,
  rental_rate DECIMAL(4,2) NOT NULL DEFAULT 4.99,
  length SMALLINT UNSIGNED DEFAULT NULL,
  replacement_cost DECIMAL(5,2) NOT NULL DEFAULT 19.99,
  rating ENUM('G','PG','PG-13','R','NC-17') DEFAULT 'G',
  special_features SET('Trailers','Commentaries','Deleted Scenes','Behind the Scenes') DEFAULT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY  (film_id),
  KEY idx_title (title),
  KEY idx_fk_language_id (language_id),
  KEY idx_fk_original_language_id (original_language_id),
  CONSTRAINT fk_film_language FOREIGN KEY (language_id) REFERENCES language (language_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_film_language_original FOREIGN KEY (original_language_id) REFERENCES language (language_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `film_actor`
--

CREATE TABLE film_actor (
  actor_id INT UNSIGNED NOT NULL,
  film_id INT UNSIGNED NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY  (actor_id,film_id),
  KEY idx_fk_film_id (`film_id`),
  CONSTRAINT fk_film_actor_actor FOREIGN KEY (actor_id) REFERENCES actor (actor_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_film_actor_film FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `film_category`
--

CREATE TABLE film_category (
  film_id INT UNSIGNED NOT NULL,
  category_id INT UNSIGNED NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (film_id, category_id),
  CONSTRAINT fk_film_category_film FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_film_category_category FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `film_text`
--

CREATE TABLE film_text (
  film_id INT NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  PRIMARY KEY  (film_id),
  FULLTEXT KEY idx_title_description (title,description)
)ENGINE=MyISAM DEFAULT CHARSET=utf8;



--
-- Table structure for table `inventory`
--

CREATE TABLE inventory (
  inventory_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  film_id INT UNSIGNED NOT NULL,
  store_id INT UNSIGNED NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY  (inventory_id),
  KEY idx_fk_film_id (film_id),
  KEY idx_store_id_film_id (store_id,film_id),
  CONSTRAINT fk_inventory_store FOREIGN KEY (store_id) REFERENCES store (store_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_inventory_film FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `language`
--

CREATE TABLE language (
  language_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  name CHAR(20) NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (language_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `payment`
--

CREATE TABLE payment (
  payment_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  customer_id INT UNSIGNED NOT NULL,
  staff_id INT UNSIGNED NOT NULL,
  rental_id INT DEFAULT NULL,
  amount DECIMAL(5,2) NOT NULL,
  payment_date DATETIME NOT NULL,
  last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY  (payment_id),
  KEY idx_fk_staff_id (staff_id),
  KEY idx_fk_customer_id (customer_id),
  CONSTRAINT fk_payment_rental FOREIGN KEY (rental_id) REFERENCES rental (rental_id) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_payment_customer FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_payment_staff FOREIGN KEY (staff_id) REFERENCES staff (staff_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `rental`
--

CREATE TABLE rental (
  rental_id INT NOT NULL AUTO_INCREMENT,
  rental_date DATETIME NOT NULL,
  inventory_id INT UNSIGNED NOT NULL,
  customer_id INT UNSIGNED NOT NULL,
  return_date DATETIME DEFAULT NULL,
  staff_id INT UNSIGNED NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (rental_id),
  UNIQUE KEY  (rental_date,inventory_id,customer_id),
  KEY idx_fk_inventory_id (inventory_id),
  KEY idx_fk_customer_id (customer_id),
  KEY idx_fk_staff_id (staff_id),
  CONSTRAINT fk_rental_staff FOREIGN KEY (staff_id) REFERENCES staff (staff_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_rental_inventory FOREIGN KEY (inventory_id) REFERENCES inventory (inventory_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_rental_customer FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `staff`
--

CREATE TABLE staff (
  staff_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(45) NOT NULL,
  last_name VARCHAR(45) NOT NULL,
  address_id INT UNSIGNED NOT NULL,
  picture MEDIUMBLOB DEFAULT NULL,
  email VARCHAR(50) DEFAULT NULL,
  store_id INT UNSIGNED NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  username VARCHAR(16) NOT NULL,
  password VARCHAR(40) BINARY DEFAULT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY  (staff_id),
  KEY idx_fk_store_id (store_id),
  KEY idx_fk_address_id (address_id),
  CONSTRAINT fk_staff_store FOREIGN KEY (store_id) REFERENCES store (store_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_staff_address FOREIGN KEY (address_id) REFERENCES address (address_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `store`
--

CREATE TABLE store (
  store_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  manager_staff_id INT UNSIGNED NOT NULL,
  address_id INT UNSIGNED NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY  (store_id),
  UNIQUE KEY idx_unique_manager (manager_staff_id),
  KEY idx_fk_address_id (address_id),
  CONSTRAINT fk_store_staff FOREIGN KEY (manager_staff_id) REFERENCES staff (staff_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_store_address FOREIGN KEY (address_id) REFERENCES address (address_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- View structure for view `customer_list`
--

CREATE VIEW customer_list
AS
SELECT cu.customer_id AS ID, CONCAT(cu.first_name, _utf8' ', cu.last_name) AS name, a.address AS address, a.postal_code AS `zip code`,
	a.phone AS phone, city.city AS city, country.country AS country, IF(cu.active, _utf8'active',_utf8'') AS notes, cu.store_id AS SID
FROM customer AS cu JOIN address AS a ON cu.address_id = a.address_id JOIN city ON a.city_id = city.city_id
	JOIN country ON city.country_id = country.country_id;

--
-- View structure for view `film_list`
--

CREATE VIEW film_list
AS
SELECT film.film_id AS FID, film.title AS title, film.description AS description, category.name AS category, film.rental_rate AS price,
	film.length AS length, film.rating AS rating, GROUP_CONCAT(CONCAT(actor.first_name, _utf8' ', actor.last_name) SEPARATOR ', ') AS actors
FROM category LEFT JOIN film_category ON category.category_id = film_category.category_id LEFT JOIN film ON film_category.film_id = film.film_id
        JOIN film_actor ON film.film_id = film_actor.film_id
	JOIN actor ON film_actor.actor_id = actor.actor_id
GROUP BY film.film_id, film.title, film.description, film.rental_rate, film.length, film.rating, category.name;

--
-- View structure for view `nicer_but_slower_film_list`
--

CREATE VIEW nicer_but_slower_film_list
AS
SELECT film.film_id AS FID, film.title AS title, film.description AS description, category.name AS category, film.rental_rate AS price,
	film.length AS length, film.rating AS rating, GROUP_CONCAT(CONCAT(CONCAT(UCASE(SUBSTR(actor.first_name,1,1)),
	LCASE(SUBSTR(actor.first_name,2,LENGTH(actor.first_name))),_utf8' ',CONCAT(UCASE(SUBSTR(actor.last_name,1,1)),
	LCASE(SUBSTR(actor.last_name,2,LENGTH(actor.last_name)))))) SEPARATOR ', ') AS actors
FROM category LEFT JOIN film_category ON category.category_id = film_category.category_id LEFT JOIN film ON film_category.film_id = film.film_id
        JOIN film_actor ON film.film_id = film_actor.film_id
	JOIN actor ON film_actor.actor_id = actor.actor_id
GROUP BY film.film_id, film.title, film.description, film.rental_rate, film.length, film.rating, category.name;

--
-- View structure for view `staff_list`
--

CREATE VIEW staff_list
AS
SELECT s.staff_id AS ID, CONCAT(s.first_name, _utf8' ', s.last_name) AS name, a.address AS address, a.postal_code AS `zip code`, a.phone AS phone,
	city.city AS city, country.country AS country, s.store_id AS SID
FROM staff AS s JOIN address AS a ON s.address_id = a.address_id JOIN city ON a.city_id = city.city_id
	JOIN country ON city.country_id = country.country_id;

--
-- View structure for view `sales_by_store`
--

CREATE VIEW sales_by_store
AS
SELECT
CONCAT(c.city, _utf8',', cy.country) AS store
, CONCAT(m.first_name, _utf8' ', m.last_name) AS manager
, SUM(p.amount) AS total_sales
FROM payment AS p
INNER JOIN rental AS r ON p.rental_id = r.rental_id
INNER JOIN inventory AS i ON r.inventory_id = i.inventory_id
INNER JOIN store AS s ON i.store_id = s.store_id
INNER JOIN address AS a ON s.address_id = a.address_id
INNER JOIN city AS c ON a.city_id = c.city_id
INNER JOIN country AS cy ON c.country_id = cy.country_id
INNER JOIN staff AS m ON s.manager_staff_id = m.staff_id
GROUP BY s.store_id
ORDER BY cy.country, c.city;

--
-- View structure for view `sales_by_film_category`
--
-- Note that total sales will add up to >100% because
-- some titles belong to more than 1 category
--

CREATE VIEW sales_by_film_category
AS
SELECT
c.name AS category
, SUM(p.amount) AS total_sales
FROM payment AS p
INNER JOIN rental AS r ON p.rental_id = r.rental_id
INNER JOIN inventory AS i ON r.inventory_id = i.inventory_id
INNER JOIN film AS f ON i.film_id = f.film_id
INNER JOIN film_category AS fc ON f.film_id = fc.film_id
INNER JOIN category AS c ON fc.category_id = c.category_id
GROUP BY c.name
ORDER BY total_sales DESC;

--
-- View structure for view `actor_info`
--

CREATE DEFINER=CURRENT_USER SQL SECURITY INVOKER VIEW actor_info
AS
SELECT
a.actor_id,
a.first_name,
a.last_name,
GROUP_CONCAT(DISTINCT CONCAT(c.name, ': ',
		(SELECT GROUP_CONCAT(f.title ORDER BY f.title SEPARATOR ', ')
                    FROM sakila.film f
                    INNER JOIN sakila.film_category fc
                      ON f.film_id = fc.film_id
                    INNER JOIN sakila.film_actor fa
                      ON f.film_id = fa.film_id
                    WHERE fc.category_id = c.category_id
                    AND fa.actor_id = a.actor_id
                 )
             )
             ORDER BY c.name SEPARATOR '; ')
AS film_info
FROM sakila.actor a
LEFT JOIN sakila.film_actor fa
  ON a.actor_id = fa.actor_id
LEFT JOIN sakila.film_category fc
  ON fa.film_id = fc.film_id
LEFT JOIN sakila.category c
  ON fc.category_id = c.category_id
GROUP BY a.actor_id, a.first_name, a.last_name;

--
-- Procedure structure for procedure `rewards_report`
--



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


USE sakila;

-- Delete data
ALTER TABLE staff DROP FOREIGN KEY fk_staff_store , DROP FOREIGN KEY fk_staff_address;
DELETE FROM payment ;
DELETE FROM rental ;
DELETE FROM customer ;
DELETE FROM film_category ;
DELETE FROM film_text ;
DELETE FROM film_actor ;
DELETE FROM inventory ;
DELETE FROM film ;
DELETE FROM category ;
ALTER TABLE store CHANGE COLUMN manager_staff_id manager_staff_id INT UNSIGNED NULL;
update store set manager_staff_id=null;
DELETE FROM staff ;
DELETE FROM store ;
DELETE FROM actor ;
DELETE FROM address ;
DELETE FROM city ;
DELETE FROM country ;
DELETE FROM language ;
ALTER TABLE store CHANGE COLUMN manager_staff_id manager_staff_id INT UNSIGNED NOT NULL;
-- Automatically generated by Advanced ETl Processor
-- http://www.etl-tools.com/
-- table language
-- Start of script
Insert into language
(`language_id`,`name`,`last_update`)
Values
    ('1','English','2006-02-15 05:02:19.000')
;
Insert into language
(`language_id`,`name`,`last_update`)
Values
    ('2','Italian','2006-02-15 05:02:19.000')
;
Insert into language
(`language_id`,`name`,`last_update`)
Values
    ('3','Japanese','2006-02-15 05:02:19.000')
;
Insert into language
(`language_id`,`name`,`last_update`)
Values
    ('4','Mandarin','2006-02-15 05:02:19.000')
;
Insert into language
(`language_id`,`name`,`last_update`)
Values
    ('5','French','2006-02-15 05:02:19.000')
;
Insert into language
(`language_id`,`name`,`last_update`)
Values
    ('6','German','2006-02-15 05:02:19.000');

Insert into country
(`country_id`,`country`,`last_update`)
Values
    ('44','India','2006-02-15 04:44:00.000')
;



Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('1','PENELOPE','GUINESS','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('2','NICK','WAHLBERG','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('3','ED','CHASE','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('4','JENNIFER','DAVIS','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('5','JOHNNY','LOLLOBRIGIDA','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('6','BETTE','NICHOLSON','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('7','GRACE','MOSTEL','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('8','MATTHEW','JOHANSSON','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('9','JOE','SWANK','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('10','CHRISTIAN','GABLE','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('11','ZERO','CAGE','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('12','KARL','BERRY','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('13','UMA','WOOD','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('14','VIVIEN','BERGEN','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('15','CUBA','OLIVIER','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('16','FRED','COSTNER','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('17','HELEN','VOIGHT','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('18','DAN','TORN','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('19','BOB','FAWCETT','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('20','LUCILLE','TRACY','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('21','KIRSTEN','PALTROW','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('22','ELVIS','MARX','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('23','SANDRA','KILMER','2006-02-15 04:34:33.000')
;
Insert into actor
(`actor_id`,`first_name`,`last_name`,`last_update`)
Values
    ('24','CAMERON','STREEP','2006-02-15 04:34:33.000')
;


-- End of Script


--
--
-- Automatically generated by Advanced ETl Processor
-- http://www.etl-tools.com/
-- table category
-- Start of script
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('1','Action','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('2','Animation','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('3','Children','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('4','Classics','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('5','Comedy','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('6','Documentary','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('7','Drama','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('8','Family','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('9','Foreign','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('10','Games','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('11','Horror','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('12','Music','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('13','New','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('14','Sci-Fi','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('15','Sports','2006-02-15 04:46:27.000')
;
Insert into category
(`category_id`,`name`,`last_update`)
Values
    ('16','Travel','2006-02-15 04:46:27.000')
;
-- End of Script
--
--
-- Automatically generated by Advanced ETl Processor
-- http://www.etl-tools.com/
-- table film
-- Start of script
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('1','ACADEMY DINOSAUR','A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies','2006','1',NULL,'6','0.99','86','20.99','PG','Deleted Scenes,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('2','ACE GOLDFINGER','A Astounding Epistle of a Database Administrator And a Explorer who must Find a Car in Ancient China','2006','1',NULL,'3','4.99','48','12.99','G','Trailers,Deleted Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('3','ADAPTATION HOLES','A Astounding Reflection of a Lumberjack And a Car who must Sink a Lumberjack in A Baloon Factory','2006','1',NULL,'7','2.99','50','18.99','NC-17','Trailers,Deleted Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('4','AFFAIR PREJUDICE','A Fanciful Documentary of a Frisbee And a Lumberjack who must Chase a Monkey in A Shark Tank','2006','1',NULL,'5','2.99','117','26.99','G','Commentaries,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('5','AFRICAN EGG','A Fast-Paced Documentary of a Pastry Chef And a Dentist who must Pursue a Forensic Psychologist in The Gulf of Mexico','2006','1',NULL,'6','2.99','130','22.99','G','Deleted Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('6','AGENT TRUMAN','A Intrepid Panorama of a Robot And a Boy who must Escape a Sumo Wrestler in Ancient China','2006','1',NULL,'3','2.99','169','17.99','PG','Deleted Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('7','AIRPLANE SIERRA','A Touching Saga of a Hunter And a Butler who must Discover a Butler in A Jet Boat','2006','1',NULL,'6','4.99','62','28.99','PG-13','Trailers,Deleted Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('8','AIRPORT POLLOCK','A Epic Tale of a Moose And a Girl who must Confront a Monkey in Ancient India','2006','1',NULL,'6','4.99','54','15.99','R','Trailers','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('9','ALABAMA DEVIL','A Thoughtful Panorama of a Database Administrator And a Mad Scientist who must Outgun a Mad Scientist in A Jet Boat','2006','1',NULL,'3','2.99','114','21.99','PG-13','Trailers,Deleted Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('10','ALADDIN CALENDAR','A Action-Packed Tale of a Man And a Lumberjack who must Reach a Feminist in Ancient China','2006','1',NULL,'6','4.99','63','24.99','NC-17','Trailers,Deleted Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('11','ALAMO VIDEOTAPE','A Boring Epistle of a Butler And a Cat who must Fight a Pastry Chef in A MySQL Convention','2006','1',NULL,'6','0.99','126','16.99','G','Commentaries,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('12','ALASKA PHANTOM','A Fanciful Saga of a Hunter And a Pastry Chef who must Vanquish a Boy in Australia','2006','1',NULL,'6','0.99','136','22.99','PG','Commentaries,Deleted Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('13','ALI FOREVER','A Action-Packed Drama of a Dentist And a Crocodile who must Battle a Feminist in The Canadian Rockies','2006','1',NULL,'4','4.99','150','21.99','PG','Deleted Scenes,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('14','ALICE FANTASIA','A Emotional Drama of a A Shark And a Database Administrator who must Vanquish a Pioneer in Soviet Georgia','2006','1',NULL,'6','0.99','94','23.99','NC-17','Trailers,Deleted Scenes,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('15','ALIEN CENTER','A Brilliant Drama of a Cat And a Mad Scientist who must Battle a Feminist in A MySQL Convention','2006','1',NULL,'5','2.99','46','10.99','NC-17','Trailers,Commentaries,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('16','ALLEY EVOLUTION','A Fast-Paced Drama of a Robot And a Composer who must Battle a Astronaut in New Orleans','2006','1',NULL,'6','2.99','180','23.99','NC-17','Trailers,Commentaries','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('17','ALONE TRIP','A Fast-Paced Character Study of a Composer And a Dog who must Outgun a Boat in An Abandoned Fun House','2006','1',NULL,'3','0.99','82','14.99','R','Trailers,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('18','ALTER VICTORY','A Thoughtful Drama of a Composer And a Feminist who must Meet a Secret Agent in The Canadian Rockies','2006','1',NULL,'6','0.99','57','27.99','PG-13','Trailers,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('19','AMADEUS HOLY','A Emotional Display of a Pioneer And a Technical Writer who must Battle a Man in A Baloon','2006','1',NULL,'6','0.99','113','20.99','PG','Commentaries,Deleted Scenes,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('20','AMELIE HELLFIGHTERS','A Boring Drama of a Woman And a Squirrel who must Conquer a Student in A Baloon','2006','1',NULL,'4','4.99','79','23.99','R','Commentaries,Deleted Scenes,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('21','AMERICAN CIRCUS','A Insightful Drama of a Girl And a Astronaut who must Face a Database Administrator in A Shark Tank','2006','1',NULL,'3','4.99','129','17.99','R','Commentaries,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('22','AMISTAD MIDSUMMER','A Emotional Character Study of a Dentist And a Crocodile who must Meet a Sumo Wrestler in California','2006','1',NULL,'6','2.99','85','10.99','G','Commentaries,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('23','ANACONDA CONFESSIONS','A Lacklusture Display of a Dentist And a Dentist who must Fight a Girl in Australia','2006','1',NULL,'3','0.99','92','9.99','R','Trailers,Deleted Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('24','ANALYZE HOOSIERS','A Thoughtful Display of a Explorer And a Pastry Chef who must Overcome a Feminist in The Sahara Desert','2006','1',NULL,'6','2.99','181','19.99','R','Trailers,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('25','ANGELS LIFE','A Thoughtful Display of a Woman And a Astronaut who must Battle a Robot in Berlin','2006','1',NULL,'3','2.99','74','15.99','G','Trailers','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('26','ANNIE IDENTITY','A Amazing Panorama of a Pastry Chef And a Boat who must Escape a Woman in An Abandoned Amusement Park','2006','1',NULL,'3','0.99','86','15.99','G','Commentaries,Deleted Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('27','ANONYMOUS HUMAN','A Amazing Reflection of a Database Administrator And a Astronaut who must Outrace a Database Administrator in A Shark Tank','2006','1',NULL,'7','0.99','179','12.99','NC-17','Deleted Scenes,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('28','ANTHEM LUKE','A Touching Panorama of a Waitress And a Woman who must Outrace a Dog in An Abandoned Amusement Park','2006','1',NULL,'5','4.99','91','16.99','PG-13','Deleted Scenes,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('29','ANTITRUST TOMATOES','A Fateful Yarn of a Womanizer And a Feminist who must Succumb a Database Administrator in Ancient India','2006','1',NULL,'5','2.99','168','11.99','NC-17','Trailers,Commentaries,Deleted Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('30','ANYTHING SAVANNAH','A Epic Story of a Pastry Chef And a Woman who must Chase a Feminist in An Abandoned Fun House','2006','1',NULL,'4','2.99','82','27.99','R','Trailers,Deleted Scenes,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('31','APACHE DIVINE','A Awe-Inspiring Reflection of a Pastry Chef And a Teacher who must Overcome a Sumo Wrestler in A U-Boat','2006','1',NULL,'5','4.99','92','16.99','NC-17','Commentaries,Deleted Scenes,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('32','APOCALYPSE FLAMINGOS','A Astounding Story of a Dog And a Squirrel who must Defeat a Woman in An Abandoned Amusement Park','2006','1',NULL,'6','4.99','119','11.99','R','Trailers,Commentaries','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('33','APOLLO TEEN','A Action-Packed Reflection of a Crocodile And a Explorer who must Find a Sumo Wrestler in An Abandoned Mine Shaft','2006','1',NULL,'5','2.99','153','15.99','PG-13','Trailers,Commentaries,Deleted Scenes,Behind the Scenes','2006-02-15 05:03:42.000')
;
Insert into film
(`film_id`,`title`,`description`,`release_year`,`language_id`,`original_language_id`,`rental_duration`,`rental_rate`,`length`,`replacement_cost`,`rating`,`special_features`,`last_update`)
Values
    ('34','ARABIA DOGMA','A Touching Epistle of a Madman And a Mad Cow who must Defeat a Student in Nigeria','2006','1',NULL,'6','0.99','62','29.99','NC-17','Commentaries,Deleted Scenes','2006-02-15 05:03:42.000')
;



-- End of Script
--
--
-- ALTER TABLE staff ADD CONSTRAINT `fk_staff_address` FOREIGN KEY (`address_id`) REFERENCES `address` (`address_id`) ON UPDATE CASCADE;
-- ALTER TABLE staff ADD CONSTRAINT `fk_staff_store` FOREIGN KEY (`store_id`) REFERENCES `store` (`store_id`) ON UPDATE CASCADE;
