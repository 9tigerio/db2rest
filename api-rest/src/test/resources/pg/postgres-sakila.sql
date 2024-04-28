
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

DROP SCHEMA IF EXISTS public cascade;
CREATE SCHEMA public;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


--
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

--
-- Name: actor_actor_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE actor_actor_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

--
-- Name: country_country_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE country_country_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

ALTER TABLE public.actor_actor_id_seq OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: actor; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE actor (
                       actor_id integer DEFAULT nextval('actor_actor_id_seq'::regclass) NOT NULL,
                       first_name character varying(45) NOT NULL,
                       last_name character varying(45) NOT NULL,
                       last_update timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.actor OWNER TO postgres;


--
-- Table structure for table `director`
--

CREATE TABLE director (
        director_id bigint NOT NULL, --- this column will be filled with TSID long value
        first_name character varying(45) NOT NULL,
        last_name character varying(45) NOT NULL,
        last_update timestamp without time zone DEFAULT now() NOT NULL
);

ALTER TABLE public.director OWNER TO postgres;

--
-- Name: director_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY director
    ADD CONSTRAINT director_pkey PRIMARY KEY (director_id);



CREATE TABLE vanity_van (
                vanity_van_id varchar(20) NOT NULL, --- this column will be filled with TSID string value
                name character varying(45) NOT NULL,
                last_update timestamp without time zone DEFAULT now() NOT NULL
);

ALTER TABLE public.vanity_van OWNER TO postgres;

--
-- Name: director_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY vanity_van
    ADD CONSTRAINT vanity_van_pkey PRIMARY KEY (vanity_van_id);





--
-- Table structure for table `review`
--

CREATE TABLE review (
          review_id character varying(20) NOT NULL, --- this column will be filled with TSID string value
          message character varying(100) NOT NULL,
          rating int NOT NULL,
          film_id int NOT NULL,
          last_update timestamp without time zone DEFAULT now() NOT NULL
);

ALTER TABLE public.review OWNER TO postgres;

ALTER TABLE ONLY review
    ADD CONSTRAINT review_id_pkey PRIMARY KEY (review_id);

--
-- Name: year; Type: DOMAIN; Schema: public; Owner: postgres
--

CREATE DOMAIN year AS integer
    CONSTRAINT year_check CHECK (((VALUE >= 1901) AND (VALUE <= 2155)));


ALTER DOMAIN public.year OWNER TO postgres;

--
-- Name: category_category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE category_category_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.category_category_id_seq OWNER TO postgres;

--
-- Name: category; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE category (
                          category_id integer DEFAULT nextval('category_category_id_seq'::regclass) NOT NULL,
                          name character varying(25) NOT NULL,
                          last_update timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.category OWNER TO postgres;

--
-- Name: film_film_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE film_film_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.film_film_id_seq OWNER TO postgres;

--
-- Name: film; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE film (
                      film_id integer DEFAULT nextval('film_film_id_seq'::regclass) NOT NULL,
                      title character varying(255) NOT NULL,
                      description text,
                      release_year year,
                      language_id integer NOT NULL,
                      original_language_id integer,
                      rental_duration smallint DEFAULT 3 NOT NULL,
                      rental_rate numeric(4,2) DEFAULT 4.99 NOT NULL,
                      length smallint,
                      replacement_cost numeric(5,2) DEFAULT 19.99 NOT NULL,
                      rating varchar(100) DEFAULT 'G',
                      last_update timestamp without time zone DEFAULT now() NOT NULL,
                      special_features varchar(300),
                      fulltext tsvector,
                      prequel_film_id integer DEFAULT NULL
);


ALTER TABLE public.film OWNER TO postgres;

--
-- Name: film_actor; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE film_actor (
                            actor_id integer NOT NULL,
                            film_id integer NOT NULL,
                            last_update timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.film_actor OWNER TO postgres;


--
-- Table structure for table `users`
--

CREATE TABLE users (
           auid integer NOT NULL,
           username varchar(100) NOT NULL,
           password varchar(150) NOT NULL,
           createdate timestamp without time zone DEFAULT now() NOT NULL,
           isActive boolean NOT NULL default true
);

ALTER TABLE public.users OWNER TO postgres;


--
-- Table structure for table `userprofile`
--

CREATE TABLE userprofile (
                 apid integer NOT NULL,
                 auid integer NOT NULL,
                 firstname varchar(50) NOT NULL,
                 lastname varchar(50) NOT NULL,
                 email varchar(100) NOT NULL,
                 phone varchar(45) NOT NULL
);

ALTER TABLE public.userprofile OWNER TO postgres;


--
-- Table structure for table `tops`
--

CREATE TABLE tops (
      top_item varchar(30) NOT NULL,
      color varchar(30) NOT NULL,
      size varchar(2) NOT NULL
);

ALTER TABLE public.tops OWNER TO postgres;

--
-- Table structure for table `bottoms`
--

CREATE TABLE bottoms (
     bottom_item varchar(30) NOT NULL,
     color varchar(30) NOT NULL,
     size varchar(2) NOT NULL
);

ALTER TABLE public.bottoms OWNER TO postgres;

--
-- Name: film_category; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE film_category (
                               film_id integer NOT NULL,
                               category_id integer NOT NULL,
                               last_update timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.film_category OWNER TO postgres;

--
-- Name: language_language_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE language_language_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.language_language_id_seq OWNER TO postgres;

--
-- Name: language; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE language (
                          language_id integer DEFAULT nextval('language_language_id_seq'::regclass) NOT NULL,
                          name character(20) NOT NULL,
                          last_update timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.language OWNER TO postgres;

--
-- Name: country; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE country (
    country_id integer DEFAULT nextval('country_country_id_seq'::regclass) NOT NULL,
    country character varying(50) NOT NULL,
    last_update timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.country OWNER TO postgres;

--
-- Name: actor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY actor
    ADD CONSTRAINT actor_pkey PRIMARY KEY (actor_id);

--
-- Name: category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY category
    ADD CONSTRAINT category_pkey PRIMARY KEY (category_id);

--
-- Name: film_actor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY film_actor
    ADD CONSTRAINT film_actor_pkey PRIMARY KEY (actor_id, film_id);


--
-- Name: film_category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY film_category
    ADD CONSTRAINT film_category_pkey PRIMARY KEY (film_id, category_id);


--
-- Name: film_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY film
    ADD CONSTRAINT film_pkey PRIMARY KEY (film_id);

--
-- Name: language_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY language
    ADD CONSTRAINT language_pkey PRIMARY KEY (language_id);

--
-- Name: country_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY country
    ADD CONSTRAINT country_pkey PRIMARY KEY (country_id);

--
-- Name: film_fulltext_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX film_fulltext_idx ON film USING gist (fulltext);


--
-- Name: idx_actor_last_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_actor_last_name ON actor USING btree (last_name);

--
-- Name: idx_fk_film_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_film_id ON film_actor USING btree (film_id);

--
-- Name: idx_fk_language_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_language_id ON film USING btree (language_id);


--
-- Name: idx_fk_original_language_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_original_language_id ON film USING btree (original_language_id);

--
-- Name: idx_title; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_title ON film USING btree (title);

--
-- Name: film_actor_actor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY film_actor
    ADD CONSTRAINT film_actor_actor_id_fkey FOREIGN KEY (actor_id) REFERENCES actor(actor_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: film_actor_film_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY film_actor
    ADD CONSTRAINT film_actor_film_id_fkey FOREIGN KEY (film_id) REFERENCES film(film_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: film_category_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY film_category
    ADD CONSTRAINT film_category_category_id_fkey FOREIGN KEY (category_id) REFERENCES category(category_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: film_category_film_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY film_category
    ADD CONSTRAINT film_category_film_id_fkey FOREIGN KEY (film_id) REFERENCES film(film_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: film_language_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY film
    ADD CONSTRAINT film_language_id_fkey FOREIGN KEY (language_id) REFERENCES language(language_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: film_original_language_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY film
    ADD CONSTRAINT film_original_language_id_fkey FOREIGN KEY (original_language_id) REFERENCES language(language_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

          --
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

--
-- Name: actor_actor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('actor_actor_id_seq', 200, true);


--
-- Name: category_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('category_category_id_seq', 16, true);


--
-- Name: film_film_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('film_film_id_seq', 1000, true);

--
-- Name: language_language_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('language_language_id_seq', 6, true);

--
-- Stored procedure; Schema: public; Owner: postgres
--

CREATE OR REPLACE PROCEDURE GetMovieRentalRateProc(movieTitle IN varchar, rentalRate OUT numeric)
	LANGUAGE plpgsql
AS  $$
	BEGIN
		SELECT rental_rate INTO rentalRate FROM film WHERE title = movieTitle;
	END;
$$;
-- call GetMovieRentalRateProc('ACADEMY DINOSAUR', null);

--
-- Function; Schema: public; Owner: postgres
--

CREATE OR REPLACE FUNCTION GetMovieRentalRateFunc(movieTitle varchar)
    RETURNS numeric
	LANGUAGE plpgsql
AS $$
	DECLARE
		rentalRate numeric;
	BEGIN
		SELECT rental_rate INTO rentalRate FROM film WHERE title = movieTitle;
		return rentalRate;
	END;
$$;
-- select GetMovieRentalRateFunc('ACADEMY DINOSAUR');
