-- Sakila Sample Database Schema for SQLite
-- Adapted from MySQL Sakila Database

-- Enable foreign key constraints
PRAGMA foreign_keys = ON;

--
-- Table structure for table `employee`
--
CREATE TABLE employee (
    emp_id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(150) NOT NULL,
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT 1
);

--
-- Table structure for table `department`
--
CREATE TABLE department (
    dept_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT 1
);

--
-- Table structure for table `tops`
--
CREATE TABLE tops (
    top_item VARCHAR(30) NOT NULL,
    color VARCHAR(30) NOT NULL,
    size VARCHAR(2) NOT NULL
);

--
-- Table structure for table `bottoms`
--
CREATE TABLE bottoms (
    bottom_item VARCHAR(30) NOT NULL,
    color VARCHAR(30) NOT NULL,
    size VARCHAR(2) NOT NULL
);

--
-- Table structure for table `users`
--
CREATE TABLE users (
    auid INTEGER NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(150) NOT NULL,
    createdate DATETIME NOT NULL,
    isActive BOOLEAN NOT NULL
);

--
-- Table structure for table `userprofile`
--
CREATE TABLE userprofile (
    apid INTEGER NOT NULL,
    auid INTEGER NOT NULL,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(45) NOT NULL
);

--
-- Table structure for table `actor`
--
CREATE TABLE actor (
    actor_id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name VARCHAR(45) NOT NULL,
    last_name VARCHAR(45) NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_actor_last_name ON actor(last_name);

--
-- Table structure for table `director`
--
CREATE TABLE director (
    director_id INTEGER PRIMARY KEY, -- this column will be filled with TSID long value
    first_name VARCHAR(45) NOT NULL,
    last_name VARCHAR(45) NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_director_last_name ON director(last_name);

--
-- Table structure for table `vanity_van`
--
CREATE TABLE vanity_van (
    van_id VARCHAR(45) PRIMARY KEY, -- this column will be filled with TSID string value
    name VARCHAR(45) NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vanity_van_name ON vanity_van(name);

--
-- Table structure for table `review`
--
CREATE TABLE review (
    review_id VARCHAR(20) PRIMARY KEY, -- this column will be filled with TSID string value
    message VARCHAR(100) NOT NULL,
    rating INTEGER NOT NULL,
    film_id INTEGER NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--
-- Table structure for table `category`
--
CREATE TABLE category (
    category_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(25) NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--
-- Table structure for table `language`
--
CREATE TABLE language (
    language_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(20) NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--
-- Table structure for table `film`
--
CREATE TABLE film (
    film_id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT DEFAULT NULL,
    release_year INTEGER DEFAULT NULL,
    language_id INTEGER NOT NULL,
    original_language_id INTEGER DEFAULT NULL,
    rental_duration INTEGER NOT NULL DEFAULT 3,
    rental_rate DECIMAL(4,2) NOT NULL DEFAULT 4.99,
    length INTEGER DEFAULT NULL,
    replacement_cost DECIMAL(5,2) NOT NULL DEFAULT 19.99,
    rating VARCHAR(10) DEFAULT 'G', -- SQLite doesn't support ENUM, using VARCHAR
    special_features TEXT DEFAULT NULL, -- SQLite doesn't support SET, using TEXT
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    prequel_film_id INTEGER DEFAULT NULL,
    FOREIGN KEY (language_id) REFERENCES language (language_id),
    FOREIGN KEY (original_language_id) REFERENCES language (language_id)
);

CREATE INDEX idx_title ON film(title);
CREATE INDEX idx_fk_language_id ON film(language_id);
CREATE INDEX idx_fk_original_language_id ON film(original_language_id);

--
-- Table structure for table `film_actor`
--
CREATE TABLE film_actor (
    actor_id INTEGER NOT NULL,
    film_id INTEGER NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (actor_id, film_id),
    FOREIGN KEY (actor_id) REFERENCES actor (actor_id),
    FOREIGN KEY (film_id) REFERENCES film (film_id)
);

CREATE INDEX idx_fk_film_id ON film_actor(film_id);

--
-- Table structure for table `film_category`
--
CREATE TABLE film_category (
    film_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (film_id, category_id),
    FOREIGN KEY (film_id) REFERENCES film (film_id),
    FOREIGN KEY (category_id) REFERENCES category (category_id)
);

--
-- Table structure for table `country`
--
CREATE TABLE country (
    country_id INTEGER PRIMARY KEY AUTOINCREMENT,
    country VARCHAR(50) NOT NULL,
    last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--
-- Table structure for table `person`
--
CREATE TABLE person (
    person_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL,
    age INTEGER
);

-- Note: SQLite doesn't support stored procedures or functions like MySQL
-- These would need to be implemented in the application layer