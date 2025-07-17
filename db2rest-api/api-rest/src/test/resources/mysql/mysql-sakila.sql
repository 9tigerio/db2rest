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
-- Table structure for table `employee`
--

CREATE TABLE employee (
          emp_id int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
          first_name varchar(100) NOT NULL,
          last_name varchar(150) NOT NULL,
          create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          is_active tinyint(1) DEFAULT 1,
          PRIMARY KEY (emp_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `department`
--

CREATE TABLE department (
        dept_id int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
        name varchar(100) NOT NULL,
        create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        is_active tinyint(1) DEFAULT 1,
        PRIMARY KEY (dept_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `tops`
--

CREATE TABLE tops (
       top_item varchar(30) NOT NULL,
       color varchar(30) NOT NULL,
       size varchar(2) NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `bottoms`
--

CREATE TABLE bottoms (
     bottom_item varchar(30) NOT NULL,
     color varchar(30) NOT NULL,
     size varchar(2) NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `users`
--

CREATE TABLE users (
     auid int(10) UNSIGNED NOT NULL,
     username varchar(100) NOT NULL,
     password varchar(150) NOT NULL,
     createdate datetime NOT NULL,
     isActive tinyint(1) NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `userprofile`
--

CREATE TABLE userprofile (
   apid int(10) UNSIGNED NOT NULL,
   auid int(10) UNSIGNED NOT NULL,
   firstname varchar(50) NOT NULL,
   lastname varchar(50) NOT NULL,
   email varchar(100) NOT NULL,
   phone varchar(45) NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


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
-- Table structure for table `director`
--

CREATE TABLE director (
  director_id BIGINT UNSIGNED NOT NULL, --- this column will be filled with TSID long value
  first_name VARCHAR(45) NOT NULL,
  last_name VARCHAR(45) NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY  (director_id),
  KEY idx_director_last_name (last_name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `vanity_van`
--

CREATE TABLE vanity_van (
          van_id VARCHAR(45) NOT NULL, --- this column will be filled with TSID string value
          name VARCHAR(45) NOT NULL,
          last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
          PRIMARY KEY  (van_id),
          KEY idx_vanity_van_name (name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `review`
--

CREATE TABLE review (
          review_id VARCHAR(20) NOT NULL, --- this column will be filled with TSID string value
          message VARCHAR(100) NOT NULL,
          rating INT NOT NULL,
          film_id INT NOT NULL,
          last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
          PRIMARY KEY  (review_id)
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
  prequel_film_id SMALLINT UNSIGNED DEFAULT NULL,
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
-- Table structure for table `language`
--

CREATE TABLE language (
  language_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  name CHAR(20) NOT NULL,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (language_id)
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
-- Table structure for table `person`
--

CREATE TABLE person (
  person_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  age TINYINT UNSIGNED,
  PRIMARY KEY  (person_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Stored procedure
--

CREATE PROCEDURE GetMovieRentalRateProc(IN movieTitle varchar(100), OUT rentalRate DECIMAL(4, 2))
    BEGIN
        SELECT rental_rate INTO rentalRate FROM film WHERE title = movieTitle;
    END;

--
-- Function
--

CREATE FUNCTION GetMovieRentalRateFunc(movieTitle varchar(100))
    RETURNS DECIMAL(4, 2)
    DETERMINISTIC
BEGIN
        DECLARE rentalRate DECIMAL(4, 2);
        SET rentalRate = 0.00;
        SELECT rental_rate INTO rentalRate FROM film WHERE title = movieTitle;
        RETURN (rentalRate);
END;
