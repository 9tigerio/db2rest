CREATE TABLE employee
(
    emp_id      BIGINT       NOT NULL IDENTITY PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(150) NOT NULL,
    create_date DATETIME     NOT NULL DEFAULT GETDATE(),
    is_active   TINYINT      NOT NULL DEFAULT 1
);

CREATE TABLE tops
(
    top_item VARCHAR(30) NOT NULL,
    color    VARCHAR(30) NOT NULL,
    size     VARCHAR(2)  NOT NULL
);

CREATE TABLE bottoms
(
    bottom_item varchar(30) NOT NULL,
    color       varchar(30) NOT NULL,
    size        varchar(2)  NOT NULL
);

CREATE TABLE users
(
    auid       BIGINT       NOT NULL,
    username   VARCHAR(100) NOT NULL,
    password   VARCHAR(150) NOT NULL,
    createdate DATETIME     NOT NULL DEFAULT GETDATE(),
    isActive   TINYINT               DEFAULT 1
);

CREATE TABLE userprofile
(
    apid      BIGINT       NOT NULL,
    auid      BIGINT       NOT NULL,
    firstname VARCHAR(50)  NOT NULL,
    lastname  VARCHAR(50)  NOT NULL,
    email     VARCHAR(100) NOT NULL,
    phone     VARCHAR(45)  NOT NULL
);

CREATE TABLE actor
(
    actor_id    BIGINT      NOT NULL IDENTITY PRIMARY KEY,
    first_name  VARCHAR(45) NOT NULL,
    last_name   VARCHAR(45) NOT NULL,
    last_update DATETIME    NOT NULL DEFAULT GETDATE(),
    INDEX       idx_actor_last_name (last_name)
);

CREATE TABLE director
(
    director_id BIGINT      NOT NULL PRIMARY KEY, --- this column will be filled with TSID long value
    first_name  VARCHAR(45) NOT NULL,
    last_name   VARCHAR(45) NOT NULL,
    last_update DATETIME    NOT NULL DEFAULT GETDATE(),
    INDEX       idx_director_last_name (last_name)
);

CREATE TABLE vanity_van
(
    van_id      VARCHAR(45) NOT NULL PRIMARY KEY, --- this column will be filled with TSID string value
    name        VARCHAR(45) NOT NULL,
    last_update DATETIME    NOT NULL DEFAULT GETDATE(),
    INDEX       idx_vanity_van_name (name)
);

CREATE TABLE category
(
    category_id BIGINT      NOT NULL IDENTITY PRIMARY KEY,
    name        VARCHAR(25) NOT NULL,
    last_update DATETIME    NOT NULL DEFAULT GETDATE()
);

CREATE TABLE language
(
    language_id BIGINT      NOT NULL IDENTITY PRIMARY KEY,
    name        VARCHAR(20) NOT NULL,
    last_update DATETIME    NOT NULL DEFAULT GETDATE()
);

CREATE TABLE film
(
    film_id              BIGINT        NOT NULL IDENTITY PRIMARY KEY,
    title                VARCHAR(255)  NOT NULL,
    description          TEXT                                                                        DEFAULT NULL,
    release_year         VARCHAR(4)                                                                  DEFAULT NULL,
    language_id          BIGINT        NOT NULL,
    original_language_id BIGINT                                                                      DEFAULT NULL,
    rental_duration      TINYINT       NOT NULL                                                      DEFAULT 3,
    rental_rate          DECIMAL(4, 2) NOT NULL                                                      DEFAULT 4.99,
    length               SMALLINT                                                                    DEFAULT NULL,
    replacement_cost     DECIMAL(5, 2) NOT NULL                                                      DEFAULT 19.99,
    rating               VARCHAR(10)   NOT NULL CHECK (rating IN ('G', 'PG', 'PG-13', 'R', 'NC-17')) DEFAULT 'G',
    special_features     VARCHAR(100) CHECK (special_features IN
                                             ('Trailers', 'Commentaries', 'Deleted Scenes',
                                              'Behind the Scenes'))                                  DEFAULT NULL,
    last_update          DATETIME      NOT NULL                                                      DEFAULT GETDATE(),
    prequel_film_id      SMALLINT                                                                    DEFAULT NULL,
    INDEX                idx_title (title),
    INDEX                idx_fk_language_id (language_id),
    INDEX                idx_fk_original_language_id (original_language_id),
    CONSTRAINT fk_film_language FOREIGN KEY (language_id) REFERENCES language (language_id),
    CONSTRAINT fk_film_language_original FOREIGN KEY (original_language_id) REFERENCES language (language_id)
);

CREATE TABLE review
(
    review_id   VARCHAR(20)  NOT NULL PRIMARY KEY, --- this column will be filled with TSID string value
    message     VARCHAR(100) NOT NULL,
    rating      INT          NOT NULL,
    film_id     INT          NOT NULL,
    last_update DATETIME     NOT NULL DEFAULT GETDATE()
);

CREATE TABLE film_actor
(
    actor_id    BIGINT   NOT NULL,
    film_id     BIGINT   NOT NULL,
    last_update DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT pk_film_actor PRIMARY KEY (actor_id, film_id),
    CONSTRAINT fk_film_actor_actor FOREIGN KEY (actor_id) REFERENCES actor (actor_id),
    CONSTRAINT fk_film_actor_film FOREIGN KEY (film_id) REFERENCES film (film_id)
);

CREATE TABLE film_category
(
    film_id     BIGINT   NOT NULL,
    category_id BIGINT   NOT NULL,
    last_update DATETIME NOT NULL DEFAULT GETDATE(),
    PRIMARY KEY (film_id, category_id),
    INDEX       idx_fk_film_category_film (film_id),
    INDEX       idx_fk_film_category_category (category_id),
    CONSTRAINT fk_film_category_film FOREIGN KEY (film_id) REFERENCES film (film_id),
    CONSTRAINT fk_film_category_category FOREIGN KEY (category_id) REFERENCES category (category_id)
);

CREATE TABLE country
(
    country_id  BIGINT      NOT NULL IDENTITY PRIMARY KEY,
    country     VARCHAR(50) NOT NULL,
    last_update DATETIME    NOT NULL DEFAULT GETDATE()
);

CREATE TABLE person
(
  person_id BIGINT      NOT NULL IDENTITY PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  age TINYINT,
);


CREATE FUNCTION GetMovieRentalRateFunc(@movieTitle VARCHAR(100))
    RETURNS DECIMAL(4, 2)
AS
BEGIN
    DECLARE @rentalRate DECIMAL(4, 2);
    SET @rentalRate = 0.00;

    SELECT @rentalRate = rental_rate
    FROM film
    WHERE title = @movieTitle;

    RETURN (@rentalRate);
END;

CREATE PROCEDURE GetMovieRentalRateProc(
    @movieTitle VARCHAR(100),
    @rentalRate DECIMAL(4, 2) OUTPUT
) AS
BEGIN
    SELECT @rentalRate = rental_rate
    FROM film
    WHERE title = @movieTitle;
END;
