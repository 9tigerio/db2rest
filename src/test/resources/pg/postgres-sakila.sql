--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

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
-- Name: mpaa_rating; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE mpaa_rating AS ENUM (
    'G',
    'PG',
    'PG-13',
    'R',
    'NC-17'
);


ALTER TYPE public.mpaa_rating OWNER TO postgres;

--
-- Name: year; Type: DOMAIN; Schema: public; Owner: postgres
--

CREATE DOMAIN year AS integer
    CONSTRAINT year_check CHECK (((VALUE >= 1901) AND (VALUE <= 2155)));


ALTER DOMAIN public.year OWNER TO postgres;

--
-- Name: _group_concat(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _group_concat(text, text) RETURNS text
AS $_$
SELECT CASE
           WHEN $2 IS NULL THEN $1
           WHEN $1 IS NULL THEN $2
           ELSE $1 || ', ' || $2
           END
           $_$
    LANGUAGE sql IMMUTABLE;


ALTER FUNCTION public._group_concat(text, text) OWNER TO postgres;

--
-- Name: group_concat(text); Type: AGGREGATE; Schema: public; Owner: postgres
--

CREATE AGGREGATE group_concat(text) (
    SFUNC = _group_concat,
    STYPE = text
);


ALTER AGGREGATE public.group_concat(text) OWNER TO postgres;

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
                      fulltext tsvector
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
-- Name: film_category; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE film_category (
                               film_id integer NOT NULL,
                               category_id integer NOT NULL,
                               last_update timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.film_category OWNER TO postgres;

--
-- Name: actor_info; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW actor_info AS
SELECT a.actor_id, a.first_name, a.last_name, group_concat(DISTINCT (((c.name)::text || ': '::text) || (SELECT group_concat((f.title)::text) AS group_concat FROM ((film f JOIN film_category fc ON ((f.film_id = fc.film_id))) JOIN film_actor fa ON ((f.film_id = fa.film_id))) WHERE ((fc.category_id = c.category_id) AND (fa.actor_id = a.actor_id)) GROUP BY fa.actor_id))) AS film_info FROM (((actor a LEFT JOIN film_actor fa ON ((a.actor_id = fa.actor_id))) LEFT JOIN film_category fc ON ((fa.film_id = fc.film_id))) LEFT JOIN category c ON ((fc.category_id = c.category_id))) GROUP BY a.actor_id, a.first_name, a.last_name;


ALTER TABLE public.actor_info OWNER TO postgres;

--
-- Name: address_address_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE address_address_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.address_address_id_seq OWNER TO postgres;

--
-- Name: address; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE address (
                         address_id integer DEFAULT nextval('address_address_id_seq'::regclass) NOT NULL,
                         address character varying(50) NOT NULL,
                         address2 character varying(50),
                         district character varying(20) NOT NULL,
                         city_id integer NOT NULL,
                         postal_code character varying(10),
                         phone character varying(20) NOT NULL,
                         last_update timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.address OWNER TO postgres;

--
-- Name: city_city_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE city_city_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.city_city_id_seq OWNER TO postgres;

--
-- Name: city; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE city (
                      city_id integer DEFAULT nextval('city_city_id_seq'::regclass) NOT NULL,
                      city character varying(50) NOT NULL,
                      country_id integer NOT NULL,
                      last_update timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.city OWNER TO postgres;

--
-- Name: country_country_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE country_country_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.country_country_id_seq OWNER TO postgres;

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
-- Name: customer_customer_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE customer_customer_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.customer_customer_id_seq OWNER TO postgres;

--
-- Name: customer; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE customer (
                          customer_id integer DEFAULT nextval('customer_customer_id_seq'::regclass) NOT NULL,
                          store_id integer NOT NULL,
                          first_name character varying(45) NOT NULL,
                          last_name character varying(45) NOT NULL,
                          email character varying(50),
                          address_id integer NOT NULL,
                          activebool boolean DEFAULT true NOT NULL,
                          create_date date DEFAULT ('now'::text)::date NOT NULL,
                          last_update timestamp without time zone DEFAULT now(),
                          active integer
);


ALTER TABLE public.customer OWNER TO postgres;

--
-- Name: customer_list; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW customer_list AS
SELECT cu.customer_id AS id, (((cu.first_name)::text || ' '::text) || (cu.last_name)::text) AS name, a.address, a.postal_code AS "zip code", a.phone, city.city, country.country, CASE WHEN cu.activebool THEN 'active'::text ELSE ''::text END AS notes, cu.store_id AS sid FROM (((customer cu JOIN address a ON ((cu.address_id = a.address_id))) JOIN city ON ((a.city_id = city.city_id))) JOIN country ON ((city.country_id = country.country_id)));


ALTER TABLE public.customer_list OWNER TO postgres;

--
-- Name: film_list; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW film_list AS
SELECT film.film_id AS fid, film.title, film.description, category.name AS category, film.rental_rate AS price, film.length, film.rating, group_concat((((actor.first_name)::text || ' '::text) || (actor.last_name)::text)) AS actors FROM ((((category LEFT JOIN film_category ON ((category.category_id = film_category.category_id))) LEFT JOIN film ON ((film_category.film_id = film.film_id))) JOIN film_actor ON ((film.film_id = film_actor.film_id))) JOIN actor ON ((film_actor.actor_id = actor.actor_id))) GROUP BY film.film_id, film.title, film.description, category.name, film.rental_rate, film.length, film.rating;


ALTER TABLE public.film_list OWNER TO postgres;

--
-- Name: inventory_inventory_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE inventory_inventory_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.inventory_inventory_id_seq OWNER TO postgres;

--
-- Name: inventory; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE inventory (
                           inventory_id integer DEFAULT nextval('inventory_inventory_id_seq'::regclass) NOT NULL,
                           film_id integer NOT NULL,
                           store_id integer NOT NULL,
                           last_update timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.inventory OWNER TO postgres;

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
-- Name: nicer_but_slower_film_list; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW nicer_but_slower_film_list AS
SELECT film.film_id AS fid, film.title, film.description, category.name AS category, film.rental_rate AS price, film.length, film.rating, group_concat((((upper("substring"((actor.first_name)::text, 1, 1)) || lower("substring"((actor.first_name)::text, 2))) || upper("substring"((actor.last_name)::text, 1, 1))) || lower("substring"((actor.last_name)::text, 2)))) AS actors FROM ((((category LEFT JOIN film_category ON ((category.category_id = film_category.category_id))) LEFT JOIN film ON ((film_category.film_id = film.film_id))) JOIN film_actor ON ((film.film_id = film_actor.film_id))) JOIN actor ON ((film_actor.actor_id = actor.actor_id))) GROUP BY film.film_id, film.title, film.description, category.name, film.rental_rate, film.length, film.rating;


ALTER TABLE public.nicer_but_slower_film_list OWNER TO postgres;

--
-- Name: payment_payment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE payment_payment_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.payment_payment_id_seq OWNER TO postgres;

--
-- Name: payment; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE payment (
                         payment_id integer DEFAULT nextval('payment_payment_id_seq'::regclass) NOT NULL,
                         customer_id integer NOT NULL,
                         staff_id integer NOT NULL,
                         rental_id integer NOT NULL,
                         amount numeric(5,2) NOT NULL,
                         payment_date timestamp without time zone NOT NULL
);


ALTER TABLE public.payment OWNER TO postgres;

--
-- Name: payment_p2007_01; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE payment_p2007_01 (CONSTRAINT payment_p2007_01_payment_date_check CHECK (((payment_date >= '2007-01-01 00:00:00'::timestamp without time zone) AND (payment_date < '2007-02-01 00:00:00'::timestamp without time zone)))
)
    INHERITS (payment);


ALTER TABLE public.payment_p2007_01 OWNER TO postgres;

--
-- Name: payment_p2007_02; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE payment_p2007_02 (CONSTRAINT payment_p2007_02_payment_date_check CHECK (((payment_date >= '2007-02-01 00:00:00'::timestamp without time zone) AND (payment_date < '2007-03-01 00:00:00'::timestamp without time zone)))
)
    INHERITS (payment);


ALTER TABLE public.payment_p2007_02 OWNER TO postgres;

--
-- Name: payment_p2007_03; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE payment_p2007_03 (CONSTRAINT payment_p2007_03_payment_date_check CHECK (((payment_date >= '2007-03-01 00:00:00'::timestamp without time zone) AND (payment_date < '2007-04-01 00:00:00'::timestamp without time zone)))
)
    INHERITS (payment);


ALTER TABLE public.payment_p2007_03 OWNER TO postgres;

--
-- Name: payment_p2007_04; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE payment_p2007_04 (CONSTRAINT payment_p2007_04_payment_date_check CHECK (((payment_date >= '2007-04-01 00:00:00'::timestamp without time zone) AND (payment_date < '2007-05-01 00:00:00'::timestamp without time zone)))
)
    INHERITS (payment);


ALTER TABLE public.payment_p2007_04 OWNER TO postgres;

--
-- Name: payment_p2007_05; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE payment_p2007_05 (CONSTRAINT payment_p2007_05_payment_date_check CHECK (((payment_date >= '2007-05-01 00:00:00'::timestamp without time zone) AND (payment_date < '2007-06-01 00:00:00'::timestamp without time zone)))
)
    INHERITS (payment);


ALTER TABLE public.payment_p2007_05 OWNER TO postgres;

--
-- Name: payment_p2007_06; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE payment_p2007_06 (CONSTRAINT payment_p2007_06_payment_date_check CHECK (((payment_date >= '2007-06-01 00:00:00'::timestamp without time zone) AND (payment_date < '2007-07-01 00:00:00'::timestamp without time zone)))
)
    INHERITS (payment);


ALTER TABLE public.payment_p2007_06 OWNER TO postgres;

--
-- Name: rental_rental_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE rental_rental_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.rental_rental_id_seq OWNER TO postgres;

--
-- Name: rental; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE rental (
                        rental_id integer DEFAULT nextval('rental_rental_id_seq'::regclass) NOT NULL,
                        rental_date timestamp without time zone NOT NULL,
                        inventory_id integer NOT NULL,
                        customer_id integer NOT NULL,
                        return_date timestamp without time zone,
                        staff_id integer NOT NULL,
                        last_update timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.rental OWNER TO postgres;

--
-- Name: sales_by_film_category; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW sales_by_film_category AS
SELECT c.name AS category, sum(p.amount) AS total_sales FROM (((((payment p JOIN rental r ON ((p.rental_id = r.rental_id))) JOIN inventory i ON ((r.inventory_id = i.inventory_id))) JOIN film f ON ((i.film_id = f.film_id))) JOIN film_category fc ON ((f.film_id = fc.film_id))) JOIN category c ON ((fc.category_id = c.category_id))) GROUP BY c.name ORDER BY sum(p.amount) DESC;


ALTER TABLE public.sales_by_film_category OWNER TO postgres;

--
-- Name: staff_staff_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE staff_staff_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.staff_staff_id_seq OWNER TO postgres;

--
-- Name: staff; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE staff (
                       staff_id integer DEFAULT nextval('staff_staff_id_seq'::regclass) NOT NULL,
                       first_name character varying(45) NOT NULL,
                       last_name character varying(45) NOT NULL,
                       address_id integer NOT NULL,
                       email character varying(50),
                       store_id integer NOT NULL,
                       active boolean DEFAULT true NOT NULL,
                       username character varying(16) NOT NULL,
                       password character varying(40),
                       last_update timestamp without time zone DEFAULT now() NOT NULL,
                       picture bytea
);


ALTER TABLE public.staff OWNER TO postgres;

--
-- Name: store_store_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE store_store_id_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.store_store_id_seq OWNER TO postgres;

--
-- Name: store; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE store (
                       store_id integer DEFAULT nextval('store_store_id_seq'::regclass) NOT NULL,
                       manager_staff_id integer NOT NULL,
                       address_id integer NOT NULL,
                       last_update timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.store OWNER TO postgres;

--
-- Name: sales_by_store; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW sales_by_store AS
SELECT (((c.city)::text || ','::text) || (cy.country)::text) AS store, (((m.first_name)::text || ' '::text) || (m.last_name)::text) AS manager, sum(p.amount) AS total_sales FROM (((((((payment p JOIN rental r ON ((p.rental_id = r.rental_id))) JOIN inventory i ON ((r.inventory_id = i.inventory_id))) JOIN store s ON ((i.store_id = s.store_id))) JOIN address a ON ((s.address_id = a.address_id))) JOIN city c ON ((a.city_id = c.city_id))) JOIN country cy ON ((c.country_id = cy.country_id))) JOIN staff m ON ((s.manager_staff_id = m.staff_id))) GROUP BY cy.country, c.city, s.store_id, m.first_name, m.last_name ORDER BY cy.country, c.city;


ALTER TABLE public.sales_by_store OWNER TO postgres;

--
-- Name: staff_list; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW staff_list AS
SELECT s.staff_id AS id, (((s.first_name)::text || ' '::text) || (s.last_name)::text) AS name, a.address, a.postal_code AS "zip code", a.phone, city.city, country.country, s.store_id AS sid FROM (((staff s JOIN address a ON ((s.address_id = a.address_id))) JOIN city ON ((a.city_id = city.city_id))) JOIN country ON ((city.country_id = country.country_id)));


ALTER TABLE public.staff_list OWNER TO postgres;


--
-- Name: actor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY actor
    ADD CONSTRAINT actor_pkey PRIMARY KEY (actor_id);


--
-- Name: address_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY address
    ADD CONSTRAINT address_pkey PRIMARY KEY (address_id);


--
-- Name: category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY category
    ADD CONSTRAINT category_pkey PRIMARY KEY (category_id);


--
-- Name: city_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY city
    ADD CONSTRAINT city_pkey PRIMARY KEY (city_id);


--
-- Name: country_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY country
    ADD CONSTRAINT country_pkey PRIMARY KEY (country_id);


--
-- Name: customer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (customer_id);


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
-- Name: inventory_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY inventory
    ADD CONSTRAINT inventory_pkey PRIMARY KEY (inventory_id);


--
-- Name: language_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY language
    ADD CONSTRAINT language_pkey PRIMARY KEY (language_id);


--
-- Name: payment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY payment
    ADD CONSTRAINT payment_pkey PRIMARY KEY (payment_id);


--
-- Name: rental_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY rental
    ADD CONSTRAINT rental_pkey PRIMARY KEY (rental_id);


--
-- Name: staff_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY staff
    ADD CONSTRAINT staff_pkey PRIMARY KEY (staff_id);


--
-- Name: store_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY store
    ADD CONSTRAINT store_pkey PRIMARY KEY (store_id);


--
-- Name: film_fulltext_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX film_fulltext_idx ON film USING gist (fulltext);


--
-- Name: idx_actor_last_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_actor_last_name ON actor USING btree (last_name);


--
-- Name: idx_fk_address_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_address_id ON customer USING btree (address_id);


--
-- Name: idx_fk_city_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_city_id ON address USING btree (city_id);


--
-- Name: idx_fk_country_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_country_id ON city USING btree (country_id);


--
-- Name: idx_fk_customer_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_customer_id ON payment USING btree (customer_id);


--
-- Name: idx_fk_film_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_film_id ON film_actor USING btree (film_id);


--
-- Name: idx_fk_inventory_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_inventory_id ON rental USING btree (inventory_id);


--
-- Name: idx_fk_language_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_language_id ON film USING btree (language_id);


--
-- Name: idx_fk_original_language_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_original_language_id ON film USING btree (original_language_id);


--
-- Name: idx_fk_payment_p2007_01_customer_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_payment_p2007_01_customer_id ON payment_p2007_01 USING btree (customer_id);


--
-- Name: idx_fk_payment_p2007_01_staff_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_payment_p2007_01_staff_id ON payment_p2007_01 USING btree (staff_id);


--
-- Name: idx_fk_payment_p2007_02_customer_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_payment_p2007_02_customer_id ON payment_p2007_02 USING btree (customer_id);


--
-- Name: idx_fk_payment_p2007_02_staff_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_payment_p2007_02_staff_id ON payment_p2007_02 USING btree (staff_id);


--
-- Name: idx_fk_payment_p2007_03_customer_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_payment_p2007_03_customer_id ON payment_p2007_03 USING btree (customer_id);


--
-- Name: idx_fk_payment_p2007_03_staff_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_payment_p2007_03_staff_id ON payment_p2007_03 USING btree (staff_id);


--
-- Name: idx_fk_payment_p2007_04_customer_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_payment_p2007_04_customer_id ON payment_p2007_04 USING btree (customer_id);


--
-- Name: idx_fk_payment_p2007_04_staff_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_payment_p2007_04_staff_id ON payment_p2007_04 USING btree (staff_id);


--
-- Name: idx_fk_payment_p2007_05_customer_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_payment_p2007_05_customer_id ON payment_p2007_05 USING btree (customer_id);


--
-- Name: idx_fk_payment_p2007_05_staff_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_payment_p2007_05_staff_id ON payment_p2007_05 USING btree (staff_id);


--
-- Name: idx_fk_payment_p2007_06_customer_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_payment_p2007_06_customer_id ON payment_p2007_06 USING btree (customer_id);


--
-- Name: idx_fk_payment_p2007_06_staff_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_payment_p2007_06_staff_id ON payment_p2007_06 USING btree (staff_id);


--
-- Name: idx_fk_staff_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_staff_id ON payment USING btree (staff_id);


--
-- Name: idx_fk_store_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_fk_store_id ON customer USING btree (store_id);


--
-- Name: idx_last_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_last_name ON customer USING btree (last_name);


--
-- Name: idx_store_id_film_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_store_id_film_id ON inventory USING btree (store_id, film_id);


--
-- Name: idx_title; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX idx_title ON film USING btree (title);


--
-- Name: idx_unq_manager_staff_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE UNIQUE INDEX idx_unq_manager_staff_id ON store USING btree (manager_staff_id);


--
-- Name: idx_unq_rental_rental_date_inventory_id_customer_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE UNIQUE INDEX idx_unq_rental_rental_date_inventory_id_customer_id ON rental USING btree (rental_date, inventory_id, customer_id);


--
-- Name: payment_insert_p2007_01; Type: RULE; Schema: public; Owner: postgres
--

CREATE RULE payment_insert_p2007_01 AS ON INSERT TO payment WHERE ((new.payment_date >= '2007-01-01 00:00:00'::timestamp without time zone) AND (new.payment_date < '2007-02-01 00:00:00'::timestamp without time zone)) DO INSTEAD INSERT INTO payment_p2007_01 (payment_id, customer_id, staff_id, rental_id, amount, payment_date) VALUES (DEFAULT, new.customer_id, new.staff_id, new.rental_id, new.amount, new.payment_date);


--
-- Name: payment_insert_p2007_02; Type: RULE; Schema: public; Owner: postgres
--

CREATE RULE payment_insert_p2007_02 AS ON INSERT TO payment WHERE ((new.payment_date >= '2007-02-01 00:00:00'::timestamp without time zone) AND (new.payment_date < '2007-03-01 00:00:00'::timestamp without time zone)) DO INSTEAD INSERT INTO payment_p2007_02 (payment_id, customer_id, staff_id, rental_id, amount, payment_date) VALUES (DEFAULT, new.customer_id, new.staff_id, new.rental_id, new.amount, new.payment_date);


--
-- Name: payment_insert_p2007_03; Type: RULE; Schema: public; Owner: postgres
--

CREATE RULE payment_insert_p2007_03 AS ON INSERT TO payment WHERE ((new.payment_date >= '2007-03-01 00:00:00'::timestamp without time zone) AND (new.payment_date < '2007-04-01 00:00:00'::timestamp without time zone)) DO INSTEAD INSERT INTO payment_p2007_03 (payment_id, customer_id, staff_id, rental_id, amount, payment_date) VALUES (DEFAULT, new.customer_id, new.staff_id, new.rental_id, new.amount, new.payment_date);


--
-- Name: payment_insert_p2007_04; Type: RULE; Schema: public; Owner: postgres
--

CREATE RULE payment_insert_p2007_04 AS ON INSERT TO payment WHERE ((new.payment_date >= '2007-04-01 00:00:00'::timestamp without time zone) AND (new.payment_date < '2007-05-01 00:00:00'::timestamp without time zone)) DO INSTEAD INSERT INTO payment_p2007_04 (payment_id, customer_id, staff_id, rental_id, amount, payment_date) VALUES (DEFAULT, new.customer_id, new.staff_id, new.rental_id, new.amount, new.payment_date);


--
-- Name: payment_insert_p2007_05; Type: RULE; Schema: public; Owner: postgres
--

CREATE RULE payment_insert_p2007_05 AS ON INSERT TO payment WHERE ((new.payment_date >= '2007-05-01 00:00:00'::timestamp without time zone) AND (new.payment_date < '2007-06-01 00:00:00'::timestamp without time zone)) DO INSTEAD INSERT INTO payment_p2007_05 (payment_id, customer_id, staff_id, rental_id, amount, payment_date) VALUES (DEFAULT, new.customer_id, new.staff_id, new.rental_id, new.amount, new.payment_date);


--
-- Name: payment_insert_p2007_06; Type: RULE; Schema: public; Owner: postgres
--

CREATE RULE payment_insert_p2007_06 AS ON INSERT TO payment WHERE ((new.payment_date >= '2007-06-01 00:00:00'::timestamp without time zone) AND (new.payment_date < '2007-07-01 00:00:00'::timestamp without time zone)) DO INSTEAD INSERT INTO payment_p2007_06 (payment_id, customer_id, staff_id, rental_id, amount, payment_date) VALUES (DEFAULT, new.customer_id, new.staff_id, new.rental_id, new.amount, new.payment_date);


--
-- Name: film_fulltext_trigger; Type: TRIGGER; Schema: public; Owner: postgres
--


--
-- Name: address_city_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY address
    ADD CONSTRAINT address_city_id_fkey FOREIGN KEY (city_id) REFERENCES city(city_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: city_country_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY city
    ADD CONSTRAINT city_country_id_fkey FOREIGN KEY (country_id) REFERENCES country(country_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: customer_address_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY customer
    ADD CONSTRAINT customer_address_id_fkey FOREIGN KEY (address_id) REFERENCES address(address_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: customer_store_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY customer
    ADD CONSTRAINT customer_store_id_fkey FOREIGN KEY (store_id) REFERENCES store(store_id) ON UPDATE CASCADE ON DELETE RESTRICT;


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
-- Name: inventory_film_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY inventory
    ADD CONSTRAINT inventory_film_id_fkey FOREIGN KEY (film_id) REFERENCES film(film_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: inventory_store_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY inventory
    ADD CONSTRAINT inventory_store_id_fkey FOREIGN KEY (store_id) REFERENCES store(store_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: payment_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment
    ADD CONSTRAINT payment_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: payment_p2007_01_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_01
    ADD CONSTRAINT payment_p2007_01_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customer(customer_id);


--
-- Name: payment_p2007_01_rental_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_01
    ADD CONSTRAINT payment_p2007_01_rental_id_fkey FOREIGN KEY (rental_id) REFERENCES rental(rental_id);


--
-- Name: payment_p2007_01_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_01
    ADD CONSTRAINT payment_p2007_01_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES staff(staff_id);


--
-- Name: payment_p2007_02_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_02
    ADD CONSTRAINT payment_p2007_02_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customer(customer_id);


--
-- Name: payment_p2007_02_rental_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_02
    ADD CONSTRAINT payment_p2007_02_rental_id_fkey FOREIGN KEY (rental_id) REFERENCES rental(rental_id);


--
-- Name: payment_p2007_02_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_02
    ADD CONSTRAINT payment_p2007_02_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES staff(staff_id);


--
-- Name: payment_p2007_03_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_03
    ADD CONSTRAINT payment_p2007_03_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customer(customer_id);


--
-- Name: payment_p2007_03_rental_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_03
    ADD CONSTRAINT payment_p2007_03_rental_id_fkey FOREIGN KEY (rental_id) REFERENCES rental(rental_id);


--
-- Name: payment_p2007_03_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_03
    ADD CONSTRAINT payment_p2007_03_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES staff(staff_id);


--
-- Name: payment_p2007_04_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_04
    ADD CONSTRAINT payment_p2007_04_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customer(customer_id);


--
-- Name: payment_p2007_04_rental_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_04
    ADD CONSTRAINT payment_p2007_04_rental_id_fkey FOREIGN KEY (rental_id) REFERENCES rental(rental_id);


--
-- Name: payment_p2007_04_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_04
    ADD CONSTRAINT payment_p2007_04_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES staff(staff_id);


--
-- Name: payment_p2007_05_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_05
    ADD CONSTRAINT payment_p2007_05_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customer(customer_id);


--
-- Name: payment_p2007_05_rental_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_05
    ADD CONSTRAINT payment_p2007_05_rental_id_fkey FOREIGN KEY (rental_id) REFERENCES rental(rental_id);


--
-- Name: payment_p2007_05_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_05
    ADD CONSTRAINT payment_p2007_05_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES staff(staff_id);


--
-- Name: payment_p2007_06_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_06
    ADD CONSTRAINT payment_p2007_06_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customer(customer_id);


--
-- Name: payment_p2007_06_rental_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_06
    ADD CONSTRAINT payment_p2007_06_rental_id_fkey FOREIGN KEY (rental_id) REFERENCES rental(rental_id);


--
-- Name: payment_p2007_06_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment_p2007_06
    ADD CONSTRAINT payment_p2007_06_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES staff(staff_id);


--
-- Name: payment_rental_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment
    ADD CONSTRAINT payment_rental_id_fkey FOREIGN KEY (rental_id) REFERENCES rental(rental_id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- Name: payment_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment
    ADD CONSTRAINT payment_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: rental_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rental
    ADD CONSTRAINT rental_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: rental_inventory_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rental
    ADD CONSTRAINT rental_inventory_id_fkey FOREIGN KEY (inventory_id) REFERENCES inventory(inventory_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: rental_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rental
    ADD CONSTRAINT rental_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: staff_address_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY staff
    ADD CONSTRAINT staff_address_id_fkey FOREIGN KEY (address_id) REFERENCES address(address_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: staff_store_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY staff
    ADD CONSTRAINT staff_store_id_fkey FOREIGN KEY (store_id) REFERENCES store(store_id);


--
-- Name: store_address_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY store
    ADD CONSTRAINT store_address_id_fkey FOREIGN KEY (address_id) REFERENCES address(address_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: store_manager_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY store
    ADD CONSTRAINT store_manager_staff_id_fkey FOREIGN KEY (manager_staff_id) REFERENCES staff(staff_id) ON UPDATE CASCADE ON DELETE RESTRICT;


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
-- Name: address_address_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('address_address_id_seq', 605, true);


--
-- Name: city_city_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('city_city_id_seq', 600, true);


--
-- Name: country_country_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('country_country_id_seq', 109, true);


--
-- Name: customer_customer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('customer_customer_id_seq', 599, true);


--
-- Name: inventory_inventory_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('inventory_inventory_id_seq', 4581, true);


--
-- Name: language_language_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('language_language_id_seq', 6, true);


--
-- Name: payment_payment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('payment_payment_id_seq', 32098, true);


--
-- Name: rental_rental_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('rental_rental_id_seq', 16049, true);


--
-- Name: staff_staff_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('staff_staff_id_seq', 2, true);


--
-- Name: store_store_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('store_store_id_seq', 2, true);


--
-- Data for Name: actor; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE language DISABLE TRIGGER ALL;

-- Automatically generated by Advanced ETl Processor
-- http://www.etl-tools.com/
-- table language
-- Start of script

Insert into language
(language_id,name,last_update)
Values
    ('1','English','2006-02-15 05:02:19.000')
;
Insert into language
(language_id,name,last_update)
Values
    ('2','Italian','2006-02-15 05:02:19.000')
;
Insert into language
(language_id,name,last_update)
Values
    ('3','Japanese','2006-02-15 05:02:19.000')
;
Insert into language
(language_id,name,last_update)
Values
    ('4','Mandarin','2006-02-15 05:02:19.000')
;
Insert into language
(language_id,name,last_update)
Values
    ('5','French','2006-02-15 05:02:19.000')
;
Insert into language
(language_id,name,last_update)
Values
    ('6','German','2006-02-15 05:02:19.000')
;
ALTER TABLE language ENABLE TRIGGER ALL;


Insert into country
(country_id,country,last_update)
Values
    ('44','India','2006-02-15 04:44:00.000')
;


Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('1','PENELOPE','GUINESS','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('2','NICK','WAHLBERG','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('3','ED','CHASE','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('4','JENNIFER','DAVIS','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('5','JOHNNY','LOLLOBRIGIDA','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('6','BETTE','NICHOLSON','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('7','GRACE','MOSTEL','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('8','MATTHEW','JOHANSSON','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('9','JOE','SWANK','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('10','CHRISTIAN','GABLE','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('11','ZERO','CAGE','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('12','KARL','BERRY','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('13','UMA','WOOD','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('14','VIVIEN','BERGEN','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('15','CUBA','OLIVIER','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('16','FRED','COSTNER','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('17','HELEN','VOIGHT','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('18','DAN','TORN','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('19','BOB','FAWCETT','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('20','LUCILLE','TRACY','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('21','KIRSTEN','PALTROW','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('22','ELVIS','MARX','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('23','SANDRA','KILMER','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('24','CAMERON','STREEP','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('25','KEVIN','BLOOM','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('26','RIP','CRAWFORD','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('27','JULIA','MCQUEEN','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('28','WOODY','HOFFMAN','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('29','ALEC','WAYNE','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('30','SANDRA','PECK','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('31','SISSY','SOBIESKI','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('32','TIM','HACKMAN','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('33','MILLA','PECK','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('34','AUDREY','OLIVIER','2006-02-15 04:34:33.000')
;
Insert into actor
(actor_id,first_name,last_name,last_update)
Values
    ('35','JUDY','DEAN','2006-02-15 04:34:33.000')
;


Insert into category
(category_id,name,last_update)
Values
    ('1','Action','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
Values
    ('2','Animation','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
Values
    ('3','Children','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
Values
    ('4','Classics','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
Values
    ('5','Comedy','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
Values
    ('6','Documentary','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
Values
    ('7','Drama','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
Values
    ('8','Family','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
Values
    ('9','Foreign','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
Values
    ('10','Games','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
Values
    ('11','Horror','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
Values
    ('12','Music','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
Values
    ('13','New','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
Values
    ('14','Sci-Fi','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
Values
    ('15','Sports','2006-02-15 04:46:27.000')
;
Insert into category
(category_id,name,last_update)
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
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('1','ACADEMY DINOSAUR','A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies','2006','1',NULL,'6','0.99','86','20.99','PG',string_to_array('Deleted Scenes,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('2','ACE GOLDFINGER','A Astounding Epistle of a Database Administrator And a Explorer who must Find a Car in Ancient China','2006','1',NULL,'3','4.99','48','12.99','G',string_to_array('Trailers,Deleted Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('3','ADAPTATION HOLES','A Astounding Reflection of a Lumberjack And a Car who must Sink a Lumberjack in A Baloon Factory','2006','1',NULL,'7','2.99','50','18.99','NC-17',string_to_array('Trailers,Deleted Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('4','AFFAIR PREJUDICE','A Fanciful Documentary of a Frisbee And a Lumberjack who must Chase a Monkey in A Shark Tank','2006','1',NULL,'5','2.99','117','26.99','G',string_to_array('Commentaries,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('5','AFRICAN EGG','A Fast-Paced Documentary of a Pastry Chef And a Dentist who must Pursue a Forensic Psychologist in The Gulf of Mexico','2006','1',NULL,'6','2.99','130','22.99','G',string_to_array('Deleted Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('6','AGENT TRUMAN','A Intrepid Panorama of a Robot And a Boy who must Escape a Sumo Wrestler in Ancient China','2006','1',NULL,'3','2.99','169','17.99','PG',string_to_array('Deleted Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('7','AIRPLANE SIERRA','A Touching Saga of a Hunter And a Butler who must Discover a Butler in A Jet Boat','2006','1',NULL,'6','4.99','62','28.99','PG-13',string_to_array('Trailers,Deleted Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('8','AIRPORT POLLOCK','A Epic Tale of a Moose And a Girl who must Confront a Monkey in Ancient India','2006','1',NULL,'6','4.99','54','15.99','R',string_to_array('Trailers',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('9','ALABAMA DEVIL','A Thoughtful Panorama of a Database Administrator And a Mad Scientist who must Outgun a Mad Scientist in A Jet Boat','2006','1',NULL,'3','2.99','114','21.99','PG-13',string_to_array('Trailers,Deleted Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('10','ALADDIN CALENDAR','A Action-Packed Tale of a Man And a Lumberjack who must Reach a Feminist in Ancient China','2006','1',NULL,'6','4.99','63','24.99','NC-17',string_to_array('Trailers,Deleted Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('11','ALAMO VIDEOTAPE','A Boring Epistle of a Butler And a Cat who must Fight a Pastry Chef in A MySQL Convention','2006','1',NULL,'6','0.99','126','16.99','G',string_to_array('Commentaries,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('12','ALASKA PHANTOM','A Fanciful Saga of a Hunter And a Pastry Chef who must Vanquish a Boy in Australia','2006','1',NULL,'6','0.99','136','22.99','PG',string_to_array('Commentaries,Deleted Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('13','ALI FOREVER','A Action-Packed Drama of a Dentist And a Crocodile who must Battle a Feminist in The Canadian Rockies','2006','1',NULL,'4','4.99','150','21.99','PG',string_to_array('Deleted Scenes,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('14','ALICE FANTASIA','A Emotional Drama of a A Shark And a Database Administrator who must Vanquish a Pioneer in Soviet Georgia','2006','1',NULL,'6','0.99','94','23.99','NC-17',string_to_array('Trailers,Deleted Scenes,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('15','ALIEN CENTER','A Brilliant Drama of a Cat And a Mad Scientist who must Battle a Feminist in A MySQL Convention','2006','1',NULL,'5','2.99','46','10.99','NC-17',string_to_array('Trailers,Commentaries,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('16','ALLEY EVOLUTION','A Fast-Paced Drama of a Robot And a Composer who must Battle a Astronaut in New Orleans','2006','1',NULL,'6','2.99','180','23.99','NC-17',string_to_array('Trailers,Commentaries',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('17','ALONE TRIP','A Fast-Paced Character Study of a Composer And a Dog who must Outgun a Boat in An Abandoned Fun House','2006','1',NULL,'3','0.99','82','14.99','R',string_to_array('Trailers,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('18','ALTER VICTORY','A Thoughtful Drama of a Composer And a Feminist who must Meet a Secret Agent in The Canadian Rockies','2006','1',NULL,'6','0.99','57','27.99','PG-13',string_to_array('Trailers,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('19','AMADEUS HOLY','A Emotional Display of a Pioneer And a Technical Writer who must Battle a Man in A Baloon','2006','1',NULL,'6','0.99','113','20.99','PG',string_to_array('Commentaries,Deleted Scenes,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('20','AMELIE HELLFIGHTERS','A Boring Drama of a Woman And a Squirrel who must Conquer a Student in A Baloon','2006','1',NULL,'4','4.99','79','23.99','R',string_to_array('Commentaries,Deleted Scenes,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('21','AMERICAN CIRCUS','A Insightful Drama of a Girl And a Astronaut who must Face a Database Administrator in A Shark Tank','2006','1',NULL,'3','4.99','129','17.99','R',string_to_array('Commentaries,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('22','AMISTAD MIDSUMMER','A Emotional Character Study of a Dentist And a Crocodile who must Meet a Sumo Wrestler in California','2006','1',NULL,'6','2.99','85','10.99','G',string_to_array('Commentaries,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('23','ANACONDA CONFESSIONS','A Lacklusture Display of a Dentist And a Dentist who must Fight a Girl in Australia','2006','1',NULL,'3','0.99','92','9.99','R',string_to_array('Trailers,Deleted Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('24','ANALYZE HOOSIERS','A Thoughtful Display of a Explorer And a Pastry Chef who must Overcome a Feminist in The Sahara Desert','2006','1',NULL,'6','2.99','181','19.99','R',string_to_array('Trailers,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('25','ANGELS LIFE','A Thoughtful Display of a Woman And a Astronaut who must Battle a Robot in Berlin','2006','1',NULL,'3','2.99','74','15.99','G',string_to_array('Trailers',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('26','ANNIE IDENTITY','A Amazing Panorama of a Pastry Chef And a Boat who must Escape a Woman in An Abandoned Amusement Park','2006','1',NULL,'3','0.99','86','15.99','G',string_to_array('Commentaries,Deleted Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('27','ANONYMOUS HUMAN','A Amazing Reflection of a Database Administrator And a Astronaut who must Outrace a Database Administrator in A Shark Tank','2006','1',NULL,'7','0.99','179','12.99','NC-17',string_to_array('Deleted Scenes,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('28','ANTHEM LUKE','A Touching Panorama of a Waitress And a Woman who must Outrace a Dog in An Abandoned Amusement Park','2006','1',NULL,'5','4.99','91','16.99','PG-13',string_to_array('Deleted Scenes,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('29','ANTITRUST TOMATOES','A Fateful Yarn of a Womanizer And a Feminist who must Succumb a Database Administrator in Ancient India','2006','1',NULL,'5','2.99','168','11.99','NC-17',string_to_array('Trailers,Commentaries,Deleted Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('30','ANYTHING SAVANNAH','A Epic Story of a Pastry Chef And a Woman who must Chase a Feminist in An Abandoned Fun House','2006','1',NULL,'4','2.99','82','27.99','R',string_to_array('Trailers,Deleted Scenes,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('31','APACHE DIVINE','A Awe-Inspiring Reflection of a Pastry Chef And a Teacher who must Overcome a Sumo Wrestler in A U-Boat','2006','1',NULL,'5','4.99','92','16.99','NC-17',string_to_array('Commentaries,Deleted Scenes,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('32','APOCALYPSE FLAMINGOS','A Astounding Story of a Dog And a Squirrel who must Defeat a Woman in An Abandoned Amusement Park','2006','1',NULL,'6','4.99','119','11.99','R',string_to_array('Trailers,Commentaries',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('33','APOLLO TEEN','A Action-Packed Reflection of a Crocodile And a Explorer who must Find a Sumo Wrestler in An Abandoned Mine Shaft','2006','1',NULL,'5','2.99','153','15.99','PG-13',string_to_array('Trailers,Commentaries,Deleted Scenes,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('34','ARABIA DOGMA','A Touching Epistle of a Madman And a Mad Cow who must Defeat a Student in Nigeria','2006','1',NULL,'6','0.99','62','29.99','NC-17',string_to_array('Commentaries,Deleted Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('35','ARACHNOPHOBIA ROLLERCOASTER','A Action-Packed Reflection of a Pastry Chef And a Composer who must Discover a Mad Scientist in The First Manned Space Station','2006','1',NULL,'4','2.99','147','24.99','PG-13',string_to_array('Trailers,Deleted Scenes,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('36','ARGONAUTS TOWN','A Emotional Epistle of a Forensic Psychologist And a Butler who must Challenge a Waitress in An Abandoned Mine Shaft','2006','1',NULL,'7','0.99','127','12.99','PG-13',string_to_array('Trailers,Commentaries',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('37','ARIZONA BANG','A Brilliant Panorama of a Mad Scientist And a Mad Cow who must Meet a Pioneer in A Monastery','2006','1',NULL,'3','2.99','121','28.99','PG',string_to_array('Trailers,Deleted Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('38','ARK RIDGEMONT','A Beautiful Yarn of a Pioneer And a Monkey who must Pursue a Explorer in The Sahara Desert','2006','1',NULL,'6','0.99','68','25.99','NC-17',string_to_array('Trailers,Commentaries,Deleted Scenes,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('39','ARMAGEDDON LOST','A Fast-Paced Tale of a Boat And a Teacher who must Succumb a Composer in An Abandoned Mine Shaft','2006','1',NULL,'5','0.99','99','10.99','G',string_to_array('Trailers',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('40','ARMY FLINTSTONES','A Boring Saga of a Database Administrator And a Womanizer who must Battle a Waitress in Nigeria','2006','1',NULL,'4','0.99','148','22.99','R',string_to_array('Trailers,Commentaries',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('41','ARSENIC INDEPENDENCE','A Fanciful Documentary of a Mad Cow And a Womanizer who must Find a Dentist in Berlin','2006','1',NULL,'4','0.99','137','17.99','PG',string_to_array('Trailers,Deleted Scenes,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('42','ARTIST COLDBLOODED','A Stunning Reflection of a Robot And a Moose who must Challenge a Woman in California','2006','1',NULL,'5','2.99','170','10.99','NC-17',string_to_array('Trailers,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('43','ATLANTIS CAUSE','A Thrilling Yarn of a Feminist And a Hunter who must Fight a Technical Writer in A Shark Tank','2006','1',NULL,'6','2.99','170','15.99','G',string_to_array('Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('44','ATTACKS HATE','A Fast-Paced Panorama of a Technical Writer And a Mad Scientist who must Find a Feminist in An Abandoned Mine Shaft','2006','1',NULL,'5','4.99','113','21.99','PG-13',string_to_array('Trailers,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;
Insert into film
(film_id,title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,last_update)
Values
    ('45','ATTRACTION NEWTON','A Astounding Panorama of a Composer And a Frisbee who must Reach a Husband in Ancient Japan','2006','1',NULL,'5','4.99','83','14.99','PG-13',string_to_array('Trailers,Behind the Scenes',','),'2006-02-15 05:03:42.000')
;



-- End of Script
--
--
-- Automatically generated by Advanced ETl Processor
-- http://www.etl-tools.com/
-- table film_actor
-- Start of script

ALTER TABLE film_actor DISABLE TRIGGER ALL;

Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','1','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','23','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','25','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','106','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','140','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','166','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','277','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','361','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','438','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','499','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','506','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','509','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','605','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','635','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','749','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','832','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','939','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','970','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('1','980','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','3','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','31','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','47','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','105','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','132','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','145','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','226','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','249','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','314','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','321','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','357','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','369','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','399','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','458','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','481','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','485','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','518','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','540','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','550','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','555','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','561','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','742','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','754','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','811','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('2','958','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','17','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','40','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','42','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','87','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','111','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','185','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','289','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','329','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','336','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','341','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','393','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','441','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','453','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','480','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','539','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','618','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','685','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','827','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','966','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','967','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','971','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('3','996','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','23','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','25','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','56','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','62','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','79','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','87','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','355','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','379','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','398','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','463','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','490','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','616','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','635','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','691','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','712','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','714','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','721','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','798','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','832','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','858','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','909','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('4','924','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','19','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','54','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','85','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','146','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','171','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','172','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','202','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','203','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','286','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','288','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','316','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','340','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','369','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','375','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','383','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','392','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','411','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','503','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','535','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','571','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','650','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','665','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','687','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','730','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','732','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','811','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','817','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','841','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('5','865','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','29','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','53','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','60','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','70','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','112','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','164','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','165','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','193','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','256','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','451','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','503','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','509','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','517','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','519','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','605','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','692','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','826','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','892','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','902','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('6','994','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','25','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','27','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','35','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','67','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','96','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','170','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','173','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','217','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','218','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','225','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','292','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','351','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','414','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','463','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','554','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','618','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','633','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','637','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','691','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','758','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','766','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','770','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','805','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','806','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','846','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','900','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','901','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','910','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','957','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('7','959','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','47','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','115','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','158','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','179','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','195','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','205','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','255','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','263','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','321','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','396','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','458','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','523','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','532','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','554','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','752','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','769','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','771','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','859','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','895','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('8','936','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','30','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','74','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','147','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','148','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','191','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','200','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','204','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','434','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','510','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','514','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','552','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','650','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','671','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','697','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','722','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','752','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','811','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','815','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','865','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','873','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','889','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','903','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','926','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','964','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('9','974','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','1','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','9','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','191','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','236','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','251','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','366','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','477','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','480','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','522','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','530','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','587','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','694','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','703','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','716','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','782','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','914','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','929','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','930','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','964','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','966','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','980','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('10','983','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','118','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','205','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','281','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','283','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','348','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','364','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','395','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','429','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','433','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','453','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','485','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','532','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','567','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','587','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','597','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','636','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','709','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','850','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','854','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','888','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','896','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','928','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','938','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','969','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('11','988','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','16','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','17','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','34','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','37','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','91','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','92','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','107','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','155','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','177','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','208','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','213','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','216','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','243','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','344','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','400','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','416','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','420','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','457','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','513','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','540','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','593','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','631','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','635','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','672','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','716','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','728','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','812','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','838','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','871','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','880','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('12','945','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','17','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','29','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','45','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','87','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','110','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','144','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','154','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','162','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','203','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','254','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','337','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','346','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','381','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','385','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','427','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','456','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','513','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','515','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','522','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','524','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','528','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','571','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','588','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','597','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','600','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','718','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','729','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','816','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','817','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','832','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','833','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','843','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','897','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','966','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('13','998','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','154','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','187','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','232','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','241','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','253','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','255','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','258','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','284','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','292','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','370','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','415','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','417','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','418','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','454','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','472','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','475','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','495','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','536','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','537','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','612','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','688','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','759','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','764','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','847','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','856','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','890','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','908','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','919','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','948','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('14','970','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','31','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','89','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','91','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','108','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','125','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','236','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','275','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','280','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','326','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','342','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','414','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','445','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','500','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','502','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','541','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','553','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','594','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','626','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','635','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','745','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','783','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','795','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','817','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','886','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','924','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','949','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','968','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('15','985','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','80','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','87','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','101','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','121','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','155','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','177','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','218','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','221','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','267','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','269','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','271','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','280','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','287','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','345','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','438','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','453','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','455','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','456','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','503','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','548','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','582','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','583','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','717','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','758','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','779','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','886','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('16','967','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','96','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','119','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','124','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','127','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','154','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','199','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','201','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','236','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','280','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','310','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','313','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','378','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','457','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','469','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','478','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','500','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','515','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','521','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','573','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','603','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','606','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','734','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','770','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','794','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','800','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','853','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','873','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','874','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','880','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','948','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','957','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('17','959','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','44','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','84','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','144','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','172','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','268','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','279','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','280','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','321','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','386','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','460','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','462','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','484','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','536','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','561','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','612','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','717','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','808','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','842','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','863','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','883','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','917','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('18','944','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','2','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','3','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','144','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','152','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','182','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','208','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','212','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','217','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','266','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','404','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','428','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','473','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','490','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','510','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','513','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','644','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','670','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','673','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','711','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','750','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','752','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','756','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','771','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','785','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('19','877','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','1','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','54','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','63','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','140','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','146','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','165','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','231','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','243','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','269','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','274','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','348','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','366','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','445','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','478','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','492','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','499','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','527','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','531','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','538','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','589','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','643','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','652','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','663','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','714','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','717','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','757','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','784','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','863','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','962','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('20','977','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','6','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','87','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','88','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','142','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','159','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','179','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','253','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','281','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','321','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','398','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','426','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','429','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','497','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','507','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','530','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','680','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','686','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','700','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','702','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','733','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','734','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','798','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','804','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','887','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','893','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','920','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('21','983','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','9','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','23','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','56','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','89','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','111','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','146','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','291','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','294','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','349','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','369','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','418','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','430','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','483','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','491','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','495','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','536','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','600','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','634','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','648','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','688','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','731','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','742','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','775','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','802','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','912','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('22','964','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','6','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','42','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','78','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','105','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','116','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','117','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','125','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','212','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','226','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','235','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','254','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','367','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','370','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','414','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','419','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','435','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','449','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','491','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','536','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','549','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','636','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','649','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','673','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','691','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','766','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','782','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','804','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','820','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','826','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','833','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','842','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','853','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','855','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','856','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','935','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','981','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('23','997','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','3','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','83','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','112','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','126','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','148','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','164','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','178','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','194','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','199','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','242','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','256','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','277','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','335','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','405','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','463','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','515','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','585','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','603','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','653','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','704','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','781','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','829','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','832','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('24','969','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','21','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','86','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','153','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','179','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','204','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','213','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','226','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','245','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','311','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','404','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','411','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','420','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','538','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','564','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','583','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','606','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','688','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','697','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','755','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','871','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('25','914','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','9','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','21','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','34','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','90','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','93','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','103','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','147','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','186','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','201','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','225','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','241','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','327','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','329','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','340','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','345','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','390','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','392','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','529','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','544','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','564','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','635','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','644','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','682','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','688','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','715','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','732','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','758','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','764','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','795','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','821','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','885','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','904','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('26','906','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','19','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','34','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','85','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','150','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','172','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','273','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','334','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','347','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','359','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','398','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','415','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','462','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','477','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','500','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','503','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','540','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','586','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','593','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','637','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','679','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','682','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','695','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','771','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','805','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','830','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','854','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','873','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','880','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','889','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','904','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','967','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','986','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('27','996','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','14','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','43','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','58','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','74','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','96','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','107','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','259','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','263','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','287','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','358','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','502','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','532','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','551','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','574','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','597','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','619','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','625','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','652','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','679','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','743','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','790','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','793','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','816','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','827','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','835','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','879','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','908','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','953','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','973','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('28','994','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','10','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','79','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','105','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','110','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','131','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','133','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','172','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','226','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','273','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','282','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','296','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','311','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','335','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','342','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','436','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','444','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','449','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','462','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','482','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','488','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','519','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','547','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','590','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','646','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','723','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','812','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','862','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','928','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('29','944','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','1','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','53','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','64','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','69','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','77','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','87','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','260','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','262','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','286','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','292','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','301','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','318','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','321','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','357','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','565','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','732','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','797','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','838','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('30','945','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','88','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','146','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','163','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','164','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','188','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','299','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','308','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','368','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','380','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','431','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','585','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','637','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','700','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','739','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','793','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','802','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','880','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('31','978','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','65','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','84','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','103','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','112','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','136','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','197','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','199','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','219','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','309','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','312','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','401','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','427','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','431','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','523','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','567','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','585','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','606','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','651','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','667','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','669','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','815','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','928','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('32','980','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','56','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','112','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','135','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','154','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','214','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','252','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','305','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','306','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','473','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','489','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','574','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','618','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','667','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','694','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','712','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','735','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','737','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','754','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','775','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','878','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','881','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','965','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','972','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('33','993','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','43','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','90','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','119','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','125','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','172','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','182','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','244','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','336','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','389','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','393','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','438','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','493','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','502','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','525','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','668','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','720','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','779','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','788','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','794','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','836','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','846','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','853','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','929','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','950','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('34','971','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','10','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','35','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','52','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','201','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','256','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','389','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','589','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','612','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','615','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','707','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','732','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','738','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','748','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','817','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('35','914','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','15','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','81','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','171','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','231','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','245','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','283','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','380','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','381','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','387','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','390','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','410','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','426','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','427','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','453','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','466','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','484','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','493','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','499','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','569','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','590','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','600','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','714','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','715','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','716','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','731','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','875','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','915','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','931','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('36','956','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','10','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','12','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','19','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','118','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','119','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','122','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','146','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','204','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','253','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','260','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','277','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','317','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','467','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','477','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','485','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','529','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','553','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','555','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','572','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','588','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','662','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','663','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','694','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','697','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','785','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','839','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','840','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','853','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','900','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','925','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','963','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','966','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','989','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('37','997','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','24','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','111','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','160','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','176','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','223','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','241','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','274','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','335','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','338','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','353','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','448','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','450','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','458','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','501','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','516','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','547','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','583','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','618','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','619','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','705','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','793','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','827','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','839','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','853','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('38','876','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','71','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','73','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','168','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','203','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','222','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','290','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','293','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','320','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','415','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','425','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','431','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','456','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','476','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','559','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','587','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','598','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','606','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','648','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','683','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','689','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','696','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','700','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','703','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','736','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','772','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','815','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','831','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('39','920','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','1','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','11','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','34','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','107','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','128','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','163','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','177','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','223','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','233','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','326','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','374','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','394','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','396','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','463','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','466','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','494','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','521','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','723','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','737','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','744','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','747','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','754','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','799','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','835','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','868','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','869','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','887','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','933','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('40','938','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','4','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','60','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','69','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','86','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','100','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','150','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','159','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','194','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','203','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','212','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','230','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','249','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','252','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','305','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','336','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','383','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','544','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','596','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','657','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','674','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','678','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','721','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','724','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','779','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','784','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','799','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','894','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','912','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('41','942','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','24','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','139','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','309','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','320','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','333','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','500','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','502','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','505','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','527','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','535','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','546','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','568','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','648','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','665','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','673','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','687','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','713','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','738','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','798','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','861','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','865','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','867','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','876','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','890','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','907','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','922','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('42','932','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','19','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','42','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','56','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','89','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','105','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','147','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','161','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','180','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','239','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','276','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','330','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','344','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','359','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','377','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','410','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','462','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','533','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','598','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','605','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','608','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','621','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','753','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','827','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','833','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','917','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('43','958','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','58','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','84','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','88','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','94','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','109','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','176','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','242','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','273','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','322','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','420','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','434','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','490','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','591','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','598','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','604','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','699','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','751','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','784','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','825','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','854','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','875','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','878','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','883','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','896','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','902','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','937','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','944','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','952','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','982','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('44','998','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','18','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','65','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','66','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','115','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','117','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','164','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','187','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','198','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','219','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','330','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','407','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','416','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','463','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','467','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','484','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','502','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','503','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','537','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','680','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','714','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','767','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','778','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','797','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','810','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','895','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','900','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','901','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','920','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','925','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','975','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('45','978','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','38','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','51','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','174','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','254','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','296','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','319','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','407','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','448','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','456','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','463','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','478','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','538','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','540','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','567','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','731','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','766','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','768','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','820','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','829','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','830','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','836','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','889','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','980','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('46','991','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','25','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','36','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','53','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','67','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','172','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','233','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','273','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','351','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','385','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','484','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','576','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','670','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','734','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','737','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','770','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','777','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','787','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','790','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','913','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','923','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','924','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','944','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('47','973','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','99','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','101','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','134','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','150','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','164','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','211','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','245','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','267','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','287','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','295','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','312','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','315','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','345','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','349','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','428','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','506','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','545','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','559','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','570','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','599','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','645','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','705','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','757','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','792','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','922','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('48','926','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','31','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','151','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','195','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','207','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','250','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','282','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','348','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','391','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','400','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','407','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','423','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','433','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','469','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','506','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','542','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','558','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','579','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','595','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','662','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','709','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','716','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','725','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','729','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','811','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','927','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','977','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('49','980','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','111','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','178','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','243','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','248','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','274','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','288','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','303','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','306','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','327','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','372','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','401','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','417','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','420','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','437','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','476','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','504','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','520','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','552','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','591','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','621','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','632','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','645','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','672','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','717','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','732','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','795','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','829','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','840','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','897','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','918','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','924','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('50','957','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','5','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','63','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','103','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','112','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','121','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','153','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','395','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','408','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','420','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','461','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','490','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','525','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','627','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','678','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','733','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','734','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','737','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','750','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','847','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','891','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','895','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','940','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','974','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','990','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('51','993','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','20','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','92','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','96','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','108','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','203','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','249','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','341','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','376','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','388','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','407','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','424','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','474','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','515','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','517','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','584','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','596','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','664','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','675','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','689','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','714','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','812','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','878','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','879','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','915','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','951','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('52','999','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','1','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','9','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','51','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','58','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','109','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','122','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','126','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','181','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','256','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','268','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','285','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','307','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','358','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','386','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','447','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','465','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','490','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','492','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','518','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','573','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','576','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','577','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','697','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','725','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','727','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','937','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','947','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','961','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('53','980','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','84','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','129','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','150','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','184','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','285','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','292','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','301','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','348','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','489','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','510','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','524','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','546','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','600','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','636','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','649','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','658','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','754','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','764','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','842','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','858','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','861','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','913','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','970','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','988','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('54','990','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','8','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','27','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','75','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','197','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','307','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','320','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','340','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','403','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','485','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','486','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','603','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','612','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','620','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','709','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','776','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','790','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','815','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','827','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','930','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('55','963','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','63','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','87','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','226','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','236','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','298','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','307','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','354','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','383','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','417','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','421','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','457','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','462','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','474','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','521','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','593','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','728','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','750','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','769','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','781','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','795','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','844','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','851','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','862','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','868','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','892','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','893','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','936','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('56','965','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','16','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','34','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','101','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','114','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','122','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','134','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','144','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','153','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','192','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','213','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','258','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','267','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','317','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','340','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','393','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','437','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','447','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','502','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','592','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','605','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','637','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','685','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','707','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','714','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','717','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','737','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','767','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','852','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','891','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('57','918','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','48','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','68','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','119','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','128','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','135','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','175','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','199','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','235','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','242','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','243','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','254','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','306','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','316','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','417','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','426','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','460','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','477','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','541','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','549','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','551','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','553','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','578','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','602','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','632','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','635','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','638','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','698','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','726','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','755','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','800','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','856','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('58','858','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','5','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','46','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','54','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','72','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','88','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','121','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','129','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','130','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','183','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','210','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','241','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','295','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','418','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','572','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','644','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','650','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','689','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','694','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','702','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','713','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','749','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','772','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','853','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','862','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','943','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','946','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('59','984','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','31','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','85','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','133','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','142','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','177','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','179','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','186','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','222','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','235','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','239','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','253','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','262','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','297','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','299','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','334','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','376','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','423','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','436','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','493','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','534','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','551','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','658','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','665','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','679','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','754','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','771','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','783','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','784','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','805','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','830','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','835','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','928','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','952','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','971','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('60','986','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','235','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','237','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','307','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','362','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','372','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','374','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','423','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','433','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','518','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','519','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','535','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','537','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','585','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','639','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','648','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','649','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','703','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','752','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','766','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','767','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','780','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','831','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','832','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('61','990','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','6','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','42','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','54','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','100','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','101','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','129','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','198','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','211','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','231','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','272','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','295','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','337','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','375','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','385','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','393','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','398','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','406','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','413','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','428','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','445','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','457','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','465','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','688','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','707','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','719','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','951','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','981','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','988','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('62','990','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','73','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','134','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','167','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','208','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','225','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','248','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','249','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','278','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','392','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','517','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','633','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','763','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','781','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','809','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','893','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','932','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','944','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','945','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('63','981','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','3','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','10','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','37','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','87','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','88','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','124','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','197','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','280','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','291','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','307','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','335','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','345','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','448','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','469','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','471','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','506','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','543','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','557','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','569','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','572','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','597','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','616','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','646','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','694','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','832','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','852','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','860','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','921','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','925','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('64','980','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','39','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','46','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','97','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','106','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','117','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','125','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','158','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','276','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','305','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','338','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','347','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','371','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','398','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','471','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','475','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','476','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','491','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','496','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','516','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','517','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','541','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','556','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','571','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','577','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','615','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','658','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','683','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','694','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','714','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','735','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','852','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','938','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','951','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('65','965','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','55','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','143','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','207','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','226','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','229','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','230','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','283','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','300','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','342','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','350','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','361','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','376','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','424','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','434','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','553','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','608','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','676','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','697','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','706','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','725','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','769','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','793','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','829','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','871','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','909','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','915','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','928','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','951','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','957','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','960','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('66','999','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','24','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','57','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','67','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','144','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','242','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','244','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','256','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','408','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','477','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','496','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','512','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','576','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','601','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','725','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','726','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','731','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','766','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','861','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','870','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','915','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','945','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','972','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('67','981','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','9','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','45','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','133','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','161','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','205','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','213','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','215','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','255','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','296','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','315','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','325','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','331','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','347','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','357','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','378','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','380','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','386','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','396','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','435','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','497','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','607','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','654','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','665','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','671','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','706','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','747','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','834','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','839','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','840','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('68','971','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','15','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','88','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','111','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','202','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','236','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','292','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','300','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','306','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','374','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','396','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','452','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','466','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','529','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','612','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','720','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','722','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','761','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','791','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','864','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','877','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('69','914','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','50','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','53','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','92','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','202','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','227','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','249','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','290','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','304','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','343','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','414','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','453','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','466','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','504','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','584','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','628','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','654','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','725','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','823','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','834','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','856','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','869','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','953','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('70','964','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','26','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','52','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','233','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','317','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','359','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','362','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','385','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','399','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','450','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','532','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','560','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','574','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','638','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','773','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','833','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','874','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','918','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('71','956','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','34','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','144','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','237','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','249','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','286','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','296','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','325','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','331','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','405','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','450','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','550','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','609','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','623','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','636','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','640','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','665','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','718','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','743','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','757','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','773','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','854','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','865','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','938','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','956','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','964','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('72','969','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','36','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','45','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','51','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','77','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','148','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','245','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','275','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','322','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','374','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','379','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','467','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','548','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','561','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','562','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','565','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','627','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','666','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','667','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','707','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','748','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','772','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','823','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','936','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','946','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','950','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('73','998','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','28','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','44','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','117','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','185','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','192','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','203','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','263','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','321','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','415','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','484','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','503','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','537','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','543','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','617','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','626','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','637','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','663','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','704','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','720','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','747','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','780','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','804','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','834','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','836','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','848','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','872','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','902','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('74','956','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','12','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','34','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','143','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','170','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','222','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','301','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','347','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','372','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','436','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','445','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','446','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','492','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','498','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','541','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','547','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','579','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','645','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','667','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','744','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','764','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','780','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','870','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('75','920','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','60','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','66','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','68','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','95','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','122','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','187','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','223','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','234','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','251','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','348','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','444','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','464','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','474','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','498','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','568','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','604','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','606','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','642','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','648','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','650','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','709','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','760','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','765','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','781','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','850','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','862','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','866','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','870','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','912','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','935','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('76','958','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','13','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','22','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','40','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','73','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','78','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','153','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','224','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','240','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','245','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','261','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','343','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','442','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','458','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','538','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','566','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','612','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','635','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','694','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','749','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','938','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','943','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','963','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','969','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('77','993','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','86','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','239','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','260','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','261','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','265','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','301','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','387','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','393','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','428','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','457','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','505','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','520','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','530','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','549','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','552','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','599','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','670','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','674','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','689','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','762','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','767','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','811','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','852','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','880','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','963','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('78','968','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','32','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','33','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','40','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','141','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','205','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','230','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','242','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','262','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','267','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','269','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','299','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','367','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','428','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','430','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','473','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','607','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','628','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','634','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','646','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','727','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','750','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','753','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','769','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','776','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','788','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','840','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','853','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('79','916','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','69','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','118','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','124','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','175','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','207','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','212','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','260','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','262','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','280','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','341','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','342','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','343','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','362','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','436','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','475','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','553','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','619','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','622','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','680','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','687','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','688','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','709','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','788','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','807','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','858','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','888','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','941','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('80','979','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','4','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','11','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','59','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','89','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','178','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','186','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','194','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','215','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','219','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','232','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','260','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','267','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','268','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','304','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','332','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','389','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','398','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','453','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','458','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','465','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','505','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','527','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','545','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','564','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','578','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','579','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','613','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','619','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','643','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','692','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','710','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','729','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','761','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','827','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('81','910','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','17','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','33','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','104','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','143','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','188','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','242','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','247','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','290','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','306','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','316','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','344','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','453','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','468','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','480','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','497','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','503','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','527','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','551','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','561','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','750','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','787','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','802','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','838','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','839','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','870','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','877','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','893','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','911','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','954','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','978','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('82','985','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','49','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','52','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','58','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','110','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','120','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','121','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','135','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','165','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','217','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','247','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','249','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','263','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','268','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','279','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','281','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','339','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','340','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','369','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','412','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','519','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','529','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','615','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','631','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','655','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','672','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','686','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','719','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','764','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','777','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','784','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','833','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','873','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('83','932','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','19','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','39','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','46','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','175','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','238','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','281','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','290','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','312','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','317','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','413','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','414','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','460','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','479','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','491','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','529','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','540','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','566','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','574','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','589','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','616','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','646','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','703','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','729','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','764','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','782','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','809','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','830','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','843','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','887','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','975','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('84','996','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','2','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','14','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','72','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','85','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','92','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','148','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','216','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','290','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','296','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','297','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','337','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','383','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','421','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','446','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','461','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','475','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','478','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','522','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','543','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','558','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','591','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','630','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','678','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','711','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','761','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','812','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','869','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','875','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','895','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','957','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('85','960','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','137','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','163','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','196','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','216','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','249','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','303','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','331','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','364','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','391','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','432','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','482','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','486','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','519','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','520','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','548','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','623','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','631','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','636','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','752','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','760','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','808','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','857','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','878','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','893','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','905','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','923','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('86','929','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','48','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','157','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','161','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','199','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','207','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','250','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','253','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','312','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','421','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','570','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','599','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','606','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','654','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','679','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','706','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','718','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','721','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','830','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','870','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','952','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('87','961','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','4','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','76','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','87','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','128','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','170','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','193','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','234','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','304','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','602','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','620','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','668','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','717','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','785','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','819','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','839','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','881','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','908','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','929','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','940','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('88','968','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','47','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','103','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','117','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','162','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','182','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','187','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','212','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','254','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','266','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','306','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','342','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','406','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','410','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','446','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','473','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','488','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','529','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','542','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','564','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','697','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','833','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','864','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','970','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('89','976','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','2','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','11','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','100','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','197','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','212','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','262','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','303','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','330','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','363','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','374','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','384','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','385','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','391','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','406','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','433','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','442','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','451','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','520','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','529','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','542','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','586','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','633','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','663','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','676','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','771','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','817','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','838','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','855','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','858','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','868','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','880','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','901','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('90','925','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','13','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','25','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','48','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','176','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','181','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','190','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','335','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','416','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','447','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','480','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','493','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','509','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','511','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','608','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','807','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','829','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','849','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','859','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','941','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('91','982','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','90','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','94','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','103','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','104','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','123','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','137','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','207','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','229','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','338','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','381','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','436','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','443','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','453','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','470','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','505','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','512','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','543','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','545','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','547','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','553','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','564','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','568','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','618','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','662','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','686','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','699','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','712','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','728','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','802','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','825','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','838','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','889','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','929','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('92','991','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','71','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','120','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','124','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','280','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','325','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','339','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','427','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','445','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','453','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','473','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','573','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','621','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','644','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','678','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','680','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','699','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','744','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','768','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','777','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','835','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','856','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','874','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','909','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','916','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('93','982','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','13','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','60','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','76','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','122','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','153','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','193','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','206','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','228','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','270','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','275','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','320','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','322','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','337','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','354','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','402','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','428','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','457','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','473','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','475','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','512','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','517','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','521','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','533','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','540','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','548','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','551','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','712','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','713','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','724','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','775','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','788','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','950','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('94','989','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','22','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','35','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','47','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','52','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','65','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','74','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','126','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','207','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','245','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','294','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','301','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','312','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','329','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','353','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','375','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','420','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','424','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','431','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','498','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','522','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','546','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','551','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','619','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','627','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','690','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','748','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','813','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','828','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','855','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','903','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('95','923','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','8','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','36','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','40','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','54','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','58','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','66','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','134','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','209','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','244','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','320','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','430','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','452','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','486','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','572','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','590','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','661','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','778','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','832','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','846','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','874','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','945','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','968','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('96','987','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','143','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','177','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','188','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','197','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','256','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','312','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','342','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','348','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','358','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','370','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','437','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','446','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','466','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','518','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','553','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','561','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','641','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','656','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','728','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','755','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','757','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','826','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','862','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','930','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','933','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','947','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('97','951','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','66','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','72','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','81','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','87','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','107','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','120','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','183','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','194','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','212','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','297','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','607','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','634','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','686','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','705','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','710','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','721','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','725','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','734','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','738','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','765','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','782','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','824','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','829','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','912','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','955','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','985','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('98','990','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','7','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','27','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','84','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','250','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','322','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','325','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','381','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','414','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','475','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','490','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','512','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','540','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','572','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','600','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','618','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','620','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','622','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','636','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','672','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','726','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','741','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','796','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','835','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','967','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','978','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('99','982','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','17','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','118','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','250','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','411','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','414','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','513','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','563','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','642','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','714','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','718','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','759','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','779','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','815','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','846','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','850','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','872','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','877','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','909','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','919','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','944','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','967','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','979','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','991','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('100','992','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','60','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','66','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','85','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','146','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','189','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','250','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','255','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','263','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','275','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','289','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','491','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','494','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','511','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','568','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','608','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','617','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','655','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','662','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','700','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','702','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','758','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','774','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','787','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','828','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','841','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','928','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','932','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','936','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','941','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','978','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','980','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','984','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('101','988','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','20','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','34','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','53','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','123','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','124','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','194','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','200','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','205','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','268','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','326','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','329','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','334','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','351','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','418','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','431','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','446','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','485','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','517','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','521','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','526','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','529','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','544','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','600','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','605','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','606','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','624','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','631','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','712','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','728','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','744','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','796','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','802','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','810','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','828','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','837','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','845','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','852','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','958','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','979','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('102','980','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','5','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','118','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','130','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','197','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','199','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','206','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','215','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','221','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','271','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','285','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','315','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','318','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','333','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','347','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','356','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','360','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','378','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','437','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','585','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','609','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','639','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','643','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','692','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','735','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','822','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','895','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','903','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','912','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','942','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('103','956','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','19','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','39','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','40','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','59','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','70','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','136','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','156','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','184','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','198','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','233','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','259','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','287','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','309','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','313','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','394','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','401','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','463','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','506','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','516','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','583','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','600','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','607','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','657','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','677','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','739','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','892','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','904','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','926','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','945','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','984','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('104','999','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','12','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','15','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','21','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','29','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','42','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','116','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','158','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','239','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','280','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','283','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','315','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','333','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','372','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','377','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','530','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','558','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','561','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','606','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','649','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','686','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','750','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','795','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','831','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','835','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','858','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','864','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','893','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','906','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','910','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','915','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','954','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','990','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','993','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('105','994','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','44','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','83','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','108','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','126','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','136','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','166','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','189','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','194','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','204','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','229','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','241','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','345','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','365','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','399','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','439','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','457','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','469','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','500','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','505','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','559','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','566','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','585','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','639','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','654','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','659','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','675','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','687','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','752','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','763','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','780','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','858','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','866','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','881','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','894','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('106','934','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','62','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','112','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','133','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','136','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','138','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','162','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','165','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','172','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','209','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','220','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','239','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','277','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','292','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','338','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','348','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','369','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','388','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','392','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','409','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','430','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','445','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','454','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','458','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','467','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','520','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','534','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','548','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','571','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','574','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','603','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','606','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','637','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','774','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','781','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','796','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','831','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','849','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','859','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','879','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','905','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','973','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('107','977','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','1','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','6','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','9','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','137','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','208','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','219','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','242','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','278','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','302','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','350','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','378','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','379','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','495','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','507','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','517','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','561','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','567','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','648','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','652','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','655','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','673','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','693','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','696','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','702','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','721','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','733','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','741','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','744','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','887','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','892','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','894','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','920','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','958','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('108','966','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','12','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','48','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','77','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','157','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','174','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','190','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','243','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','281','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','393','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','463','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','622','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','657','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','694','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','700','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','732','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','753','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','785','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','786','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','863','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','885','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','955','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('109','967','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','8','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','27','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','62','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','120','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','126','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','156','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','292','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','343','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','360','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','369','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','435','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','513','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','525','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','539','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','545','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','625','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','650','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','801','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','912','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','961','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('110','987','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','61','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','78','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','98','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','162','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','179','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','194','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','325','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','359','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','382','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','403','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','407','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','414','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','474','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','489','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','555','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','603','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','608','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','643','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','669','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','679','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','680','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','699','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','731','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','732','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','737','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','744','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','777','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','847','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','894','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','919','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','962','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('111','973','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','34','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','37','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','151','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','173','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','188','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','231','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','312','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','322','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','443','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','450','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','565','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','603','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','606','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','654','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','666','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','700','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','728','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','772','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','796','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','817','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','829','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','856','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','865','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','869','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('112','988','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','35','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','84','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','116','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','181','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','218','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','249','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','258','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','292','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','322','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','353','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','403','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','525','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','642','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','656','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','674','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','680','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','700','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','719','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','723','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','726','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','732','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','748','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','838','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','890','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','921','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','969','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('113','981','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','13','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','68','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','90','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','162','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','188','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','194','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','210','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','237','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','254','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','305','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','339','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','420','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','425','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','452','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','538','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','619','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','757','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','807','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','827','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','841','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','861','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','866','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','913','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','961','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('114','993','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','49','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','52','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','245','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','246','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','277','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','302','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','379','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','383','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','391','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','428','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','506','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','531','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','607','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','615','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','661','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','671','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','686','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','703','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','714','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','740','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','754','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','846','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','887','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','952','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','955','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','966','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','985','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('115','994','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','36','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','48','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','88','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','90','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','105','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','128','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','336','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','338','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','384','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','412','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','420','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','451','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','481','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','492','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','584','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','606','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','622','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','647','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','653','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','742','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','784','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','844','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','939','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('116','956','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','10','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','15','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','42','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','167','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','178','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','190','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','197','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','224','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','246','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','273','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','298','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','316','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','337','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','395','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','423','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','432','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','459','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','468','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','550','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','578','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','707','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','710','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','738','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','739','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','778','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','783','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','785','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','797','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','812','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','831','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','864','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','887','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('117','926','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','35','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','39','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','41','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','49','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','55','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','136','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','141','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','151','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','311','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','384','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','399','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','499','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','517','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','553','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','558','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','572','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','641','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','656','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','695','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','735','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','788','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','852','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','938','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','957','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('118','969','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','21','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','49','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','64','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','87','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','143','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','171','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','172','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','173','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','381','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','394','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','412','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','418','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','454','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','509','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','521','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','567','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','570','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','592','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','614','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','636','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','649','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','693','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','738','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','751','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','782','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','786','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','788','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','802','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','858','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','868','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','900','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('119','939','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','57','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','63','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','144','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','149','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','208','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','231','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','238','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','255','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','414','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','424','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','489','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','513','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','590','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','641','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','642','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','659','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','682','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','691','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','715','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','717','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','722','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','746','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','830','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','894','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','898','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','911','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('120','994','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','141','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','154','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','161','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','170','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','186','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','198','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','220','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','222','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','284','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','297','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','338','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','353','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','449','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','479','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','517','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','633','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','654','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','658','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','666','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','771','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','780','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','847','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','884','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','885','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('121','966','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','22','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','29','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','76','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','83','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','157','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','158','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','166','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','227','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','238','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','300','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','307','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','363','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','470','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','489','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','491','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','542','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','620','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','649','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','654','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','673','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','718','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','795','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','957','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','961','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('122','998','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','3','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','43','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','67','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','105','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','148','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','151','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','185','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','223','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','234','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','245','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','246','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','266','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','286','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','429','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','442','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','446','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','479','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','480','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','494','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','503','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','530','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','576','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','577','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','589','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','593','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','725','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','730','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','786','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','860','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','892','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','926','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('123','988','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','22','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','64','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','106','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','113','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','190','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','246','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','260','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','263','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','289','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','306','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','312','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','322','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','343','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','449','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','468','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','539','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','601','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','726','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','742','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','775','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','785','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','814','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','858','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','882','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','987','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('124','997','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','62','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','98','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','100','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','114','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','175','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','188','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','204','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','238','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','250','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','324','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','338','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','361','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','367','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','395','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','414','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','428','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','429','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','450','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','497','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','557','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','568','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','584','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','602','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','623','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','664','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','683','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','710','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','877','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','908','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','949','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('125','965','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','21','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','34','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','43','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','58','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','85','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','96','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','193','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','194','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','199','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','256','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','263','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','288','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','317','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','347','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','369','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','370','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','419','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','468','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','469','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','545','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','685','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','836','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('126','860','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','36','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','47','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','48','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','79','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','119','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','141','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','157','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','202','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','286','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','333','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','354','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','366','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','382','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','388','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','411','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','459','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','553','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','573','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','613','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','617','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','641','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','710','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','727','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','749','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','763','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','771','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','791','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','819','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','839','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','846','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','911','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','953','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('127','970','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','26','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','82','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','119','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','168','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','212','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','238','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','299','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','312','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','326','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','336','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','345','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','407','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','462','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','485','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','516','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','564','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','614','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','650','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','665','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','671','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','693','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','696','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','759','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','774','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','814','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','899','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','912','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','944','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','949','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('128','965','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','56','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','89','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','101','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','166','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','202','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','230','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','247','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','249','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','348','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','367','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','391','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','418','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','431','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','452','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','471','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','520','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','597','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','602','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','640','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','669','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','684','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','705','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','805','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','826','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','834','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','857','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','910','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','920','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','938','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('129','962','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','9','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','26','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','37','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','43','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','49','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','57','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','107','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','112','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','208','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','326','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','375','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','416','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','431','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','452','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','453','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','478','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','507','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','525','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','549','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','592','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','702','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','725','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','764','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','809','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','869','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','930','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('130','981','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','48','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','66','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','94','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','120','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','147','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','206','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','320','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','383','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','432','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','436','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','450','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','479','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','494','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','515','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','539','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','590','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','647','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','693','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','713','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','770','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','798','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','809','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','875','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','881','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('131','921','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','81','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','82','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','133','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','156','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','162','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','311','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','345','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','377','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','410','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','538','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','562','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','586','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','626','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','637','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','698','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','756','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','806','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','897','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','899','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','904','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','930','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('132','987','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','7','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','51','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','133','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','172','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','210','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','270','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','280','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','286','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','338','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','342','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','351','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','368','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','385','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','390','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','397','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','410','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','452','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','463','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','514','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','588','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','594','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','635','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','652','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','727','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','806','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','868','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','882','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','894','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','933','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('133','952','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','132','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','145','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','161','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','219','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','243','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','250','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','278','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','341','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','386','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','413','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','558','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','588','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','624','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','655','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','683','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','690','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','861','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','896','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','897','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','915','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','927','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('134','936','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','35','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','41','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','65','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','88','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','170','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','269','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','320','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','353','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','357','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','364','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','455','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','458','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','484','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','541','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','553','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','616','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','628','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','719','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','814','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('135','905','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','20','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','25','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','33','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','56','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','61','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','193','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','214','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','229','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','243','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','256','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','262','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','271','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','288','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','300','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','364','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','401','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','414','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','420','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','474','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','485','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','542','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','552','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','620','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','649','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','686','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','781','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','806','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','808','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','818','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','842','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','933','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('136','993','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','6','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','14','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','56','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','96','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','160','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','224','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','249','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','254','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','263','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','268','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','304','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','390','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','410','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','433','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','446','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','489','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','530','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','564','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','603','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','610','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','688','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','703','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','745','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','758','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','832','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','841','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('137','917','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','8','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','52','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','61','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','125','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','157','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','214','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','258','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','376','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','403','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','446','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','453','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','553','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','561','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','583','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','627','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','639','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','695','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','747','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','879','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','885','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','923','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','970','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('138','989','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','20','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','35','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','57','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','74','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','90','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','107','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','155','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','170','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','181','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','200','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','229','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','233','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','261','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','262','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','266','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','282','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','284','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','373','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','447','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','489','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','529','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','540','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','570','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','602','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','605','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','636','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','691','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','706','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','719','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','744','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','746','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','862','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('139','892','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','27','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','77','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','112','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','135','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','185','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','258','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','370','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','373','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','498','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','509','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','576','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','587','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','599','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','608','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','647','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','665','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','670','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','693','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','702','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','729','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','730','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','731','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','736','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','742','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','778','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','820','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','830','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','835','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','857','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','923','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','934','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('140','999','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','43','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','67','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','188','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','191','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','207','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','223','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','341','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','358','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','380','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','395','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','467','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','491','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','589','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','607','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','673','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','740','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','752','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','768','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','772','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','787','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','821','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','829','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','840','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','849','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','862','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','863','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','909','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('141','992','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','10','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','18','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','107','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','139','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','186','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','199','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','248','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','328','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','350','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','371','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','470','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','481','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','494','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','501','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','504','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','540','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','554','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','575','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','608','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','710','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','712','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','735','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','759','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','794','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','842','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','859','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','863','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','875','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','906','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','914','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('142','999','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','47','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','79','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','141','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','175','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','232','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','239','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','316','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','339','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','361','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','386','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','404','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','457','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','485','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','497','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','560','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','576','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','603','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','613','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','659','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','660','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','680','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','687','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','690','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','706','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','792','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','821','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','830','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','872','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','878','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','906','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('143','958','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','18','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','67','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','79','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','90','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','99','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','105','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','123','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','125','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','127','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','130','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','135','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','164','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','184','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','216','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','228','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','260','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','272','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','291','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','293','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','312','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','393','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','396','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','473','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','504','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','540','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','599','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','668','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','702','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','753','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','762','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','776','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','785','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','845','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','894','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('144','953','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','39','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','109','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','120','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','154','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','155','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','243','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','293','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','402','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','409','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','457','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','475','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','487','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','494','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','527','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','592','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','625','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','629','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','641','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','661','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','664','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','692','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','713','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','726','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','748','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','822','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','893','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','923','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('145','953','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','12','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','16','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','33','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','117','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','177','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','191','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','197','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','207','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','218','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','278','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','296','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','314','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','320','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','372','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','384','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','402','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','410','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','427','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','429','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','512','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','514','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','571','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','591','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','720','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','731','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','734','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','871','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','909','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','922','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','945','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','955','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','966','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('146','969','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','4','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','85','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','131','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','139','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','145','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','178','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','251','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','254','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','295','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','298','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','305','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','310','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','318','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','333','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','341','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','351','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','394','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','402','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','405','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','410','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','431','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','443','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','554','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','563','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','649','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','688','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','708','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','864','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','957','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('147','987','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('148','27','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('148','57','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('148','133','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('148','149','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('148','226','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('148','342','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('148','368','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('148','422','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('148','468','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('148','633','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('148','718','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('148','768','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('148','772','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('148','792','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','53','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','72','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','95','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','118','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','139','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','146','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','153','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','159','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','169','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','178','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','188','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','193','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','339','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','354','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','362','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','365','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','458','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','631','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','670','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','685','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','761','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','782','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','810','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','811','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','899','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','905','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','913','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','921','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','947','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','949','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('149','992','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','23','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','63','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','75','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','94','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','105','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','168','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','190','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','206','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','233','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','270','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','285','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','306','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','386','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','433','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','446','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','447','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','468','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','542','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','551','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','629','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','647','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','672','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','697','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','728','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','777','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','854','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','873','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','880','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','887','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','889','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','892','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','953','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('150','962','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','131','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','144','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','167','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','170','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','217','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','232','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','342','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','367','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','370','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','382','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','451','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','463','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','482','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','501','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','527','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','539','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','570','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','574','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','634','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','658','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','665','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','703','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','880','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','892','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','895','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('151','989','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','59','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','153','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','217','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','248','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','318','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','332','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','475','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','476','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','578','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','607','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','611','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','615','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','674','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','680','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','729','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','768','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','821','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','846','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','891','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','898','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','927','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','964','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('152','968','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','47','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','64','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','136','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','180','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','203','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','231','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','444','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','476','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','480','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','486','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','536','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','627','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','732','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','756','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','766','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','817','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','847','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','919','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','938','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('153','988','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','27','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','111','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','141','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','158','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','169','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','170','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','193','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','208','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','274','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','276','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','282','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','299','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','314','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','396','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','399','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','421','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','440','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','467','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','474','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','489','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','588','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','602','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','680','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','698','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','802','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','842','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','954','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('154','988','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','20','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','67','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','128','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','153','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','220','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','249','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','303','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','312','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','359','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','361','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','383','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','387','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','407','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','427','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','459','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','513','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','584','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','590','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','630','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','688','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','757','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','768','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','785','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','849','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','885','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','890','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','941','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','966','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','987','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','997','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('155','1000','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','53','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','155','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','198','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','244','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','262','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','263','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','285','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','297','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','301','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','349','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','379','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','448','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','462','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','467','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','504','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','518','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','593','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','646','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','705','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','754','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','775','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('156','844','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','10','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','24','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','34','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','122','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','159','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','183','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','210','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','217','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','291','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','303','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','321','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','326','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','353','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','400','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','406','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','431','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','496','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','535','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','573','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','574','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','604','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','616','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','642','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','661','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','696','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','713','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','802','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','835','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','874','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','913','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','967','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('157','973','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','32','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','47','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','64','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','66','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','102','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','121','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','177','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','178','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','188','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','215','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','241','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','293','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','437','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','473','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','483','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','532','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','555','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','581','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','601','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','616','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','626','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','637','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','799','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','812','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','824','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','830','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','840','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','869','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','879','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','880','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','894','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','896','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','967','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','968','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('158','990','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','20','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','82','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','127','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','187','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','206','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','208','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','223','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','248','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','342','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','343','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','344','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','364','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','418','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','549','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','561','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','600','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','674','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','680','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','784','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','789','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','800','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','802','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','818','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','876','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','907','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('159','978','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','2','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','17','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','43','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','242','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','267','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','275','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','368','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','455','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','469','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','484','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','579','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','660','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','755','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','767','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','769','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','794','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','826','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','883','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','950','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('160','954','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','43','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','58','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','89','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','90','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','120','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','188','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','247','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','269','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','281','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','340','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','353','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','401','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','414','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','425','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','469','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','526','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','588','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','644','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','653','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','655','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','669','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','684','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','714','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','749','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','807','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','825','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','850','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','880','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','920','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','921','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','924','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('161','927','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','1','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','4','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','7','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','18','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','28','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','32','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','33','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','41','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','85','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','121','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','164','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','274','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','279','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','409','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','410','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','415','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','500','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','574','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','612','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','636','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','659','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','786','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','844','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','909','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('162','968','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','30','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','45','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','166','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','180','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','239','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','283','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','303','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','304','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','307','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','394','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','409','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','434','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','444','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','522','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','719','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','785','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','833','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','881','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','891','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','947','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('163','996','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','15','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','23','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','148','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','169','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','252','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','324','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','347','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','367','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','431','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','448','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','469','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','545','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','610','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','613','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','673','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','681','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','698','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','801','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','820','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','832','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','834','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','851','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','884','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','908','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','957','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('164','984','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','72','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','95','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','146','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','204','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','253','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','286','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','360','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','375','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','395','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','421','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','437','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','473','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','607','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','644','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','659','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','693','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','737','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','779','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','798','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','807','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','809','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','832','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','833','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','947','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','948','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('165','962','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','25','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','38','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','55','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','61','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','68','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','86','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','146','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','255','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','297','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','306','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','326','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','361','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','366','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','426','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','580','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','622','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','674','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','714','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','788','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','867','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','944','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('166','1000','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','17','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','25','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','63','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','72','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','107','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','120','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','191','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','294','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','319','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','339','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','341','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','496','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','554','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','626','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','628','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','672','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','692','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','717','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','734','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','794','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','800','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','802','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','856','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','864','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','882','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('167','923','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','32','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','56','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','92','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','115','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','188','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','196','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','208','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','237','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','241','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','255','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','305','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','336','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','387','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','433','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','438','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','519','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','602','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','619','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','626','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','652','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','678','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','685','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','804','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','807','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','826','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','841','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','886','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','889','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','892','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','927','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('168','959','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','6','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','78','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','93','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','246','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','248','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','289','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','301','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','326','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','349','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','372','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','398','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','434','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','505','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','564','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','571','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','634','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','642','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','673','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','694','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','727','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','778','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','815','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','847','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','849','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','894','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','897','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','954','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','992','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('169','998','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','7','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','15','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','27','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','33','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','102','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','139','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','180','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','184','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','212','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','299','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','322','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','358','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','416','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','537','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','705','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','758','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','764','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','868','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','877','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','886','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','925','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','993','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('170','996','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','49','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','146','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','166','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','181','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','219','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','273','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','296','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','318','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','342','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','397','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','447','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','450','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','466','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','549','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','560','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','566','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','608','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','625','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','645','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','701','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','761','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','779','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','849','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','872','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','892','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','898','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','903','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('171','953','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','57','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','100','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','148','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','215','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','302','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','345','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','368','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','385','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','423','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','487','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','493','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','529','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','538','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','567','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','609','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','639','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','649','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','661','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','667','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','710','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','744','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','758','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','771','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','833','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('172','959','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','49','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','55','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','74','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','80','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','106','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','154','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','162','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','188','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','235','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','313','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','379','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','405','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','491','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','496','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','529','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','550','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','564','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','571','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','592','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','688','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','753','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','757','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','852','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','857','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','921','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','928','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('173','933','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','11','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','61','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','168','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','298','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','352','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','442','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','451','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','496','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','610','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','618','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','622','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','659','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','677','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','705','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','722','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','780','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','797','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','809','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','827','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','830','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','852','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','853','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','879','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('174','982','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','9','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','29','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','67','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','129','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','155','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','190','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','191','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','362','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','405','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','424','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','439','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','442','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','483','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','591','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','596','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','616','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','719','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','729','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','772','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','778','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','828','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','842','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','890','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','908','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','977','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','978','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('175','998','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','13','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','73','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','89','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','150','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','162','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','238','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','252','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','303','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','320','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','401','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','417','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','441','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','458','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','461','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','517','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','521','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','543','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','573','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','699','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','726','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','740','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','746','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','758','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','802','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','827','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','839','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','859','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','872','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('176','946','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','12','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','39','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','52','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','55','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','86','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','175','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','188','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','235','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','237','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','289','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','363','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','401','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','433','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','458','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','522','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','543','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','563','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','649','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','683','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','684','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','726','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','751','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','763','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','764','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','827','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','910','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('177','956','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','30','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','34','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','109','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','146','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','160','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','164','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','194','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','197','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','273','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','311','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','397','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','483','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','517','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','537','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','587','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','708','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','733','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','744','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','762','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','930','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','974','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','983','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('178','1000','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','24','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','27','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','65','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','85','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','109','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','131','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','159','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','193','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','250','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','291','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','353','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','415','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','463','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','468','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','489','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','566','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','588','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','650','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','698','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','732','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','737','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','769','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','811','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','817','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','852','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','924','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','931','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','960','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('179','976','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','12','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','33','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','144','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','195','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','258','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','441','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','506','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','561','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','609','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','622','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','628','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','657','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','724','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','729','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','732','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','777','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','809','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','811','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','820','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','824','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','847','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','869','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','874','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','955','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('180','963','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','5','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','40','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','74','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','78','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','83','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','152','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','195','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','233','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','286','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','301','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','311','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','381','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','387','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','403','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','409','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','420','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','437','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','456','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','507','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','522','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','539','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','542','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','546','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','579','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','596','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','604','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','609','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','625','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','744','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','816','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','836','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','868','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','870','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','874','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','892','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','907','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','911','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','921','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('181','991','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','33','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','160','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','301','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','324','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','346','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','362','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','391','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','413','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','421','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','437','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','590','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','639','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','668','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','677','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','679','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','695','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','714','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','720','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','819','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','828','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','845','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','864','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','940','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('182','990','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','32','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','40','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','71','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','113','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','313','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','388','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','389','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','390','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','495','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','520','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','576','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','636','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','715','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','850','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','862','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','914','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','941','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','949','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('183','983','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','35','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','87','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','146','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','169','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','221','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','336','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','371','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','452','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','486','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','492','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','500','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','574','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','580','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','597','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','615','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','640','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','642','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','650','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','661','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','684','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','745','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','772','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','787','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','867','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','959','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','966','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','967','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','969','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('184','985','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','7','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','95','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','138','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','265','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','286','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','360','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','411','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','427','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','437','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','448','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','494','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','510','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','518','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','554','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','560','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','571','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','584','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','631','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','665','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','694','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','730','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','761','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','818','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','845','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','880','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','882','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','919','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','920','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','965','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('185','973','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','95','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','187','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','208','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','228','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','237','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','422','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','482','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','508','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','552','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','579','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','637','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','648','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','654','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','729','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','983','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('186','994','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','17','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','25','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','29','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','51','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','73','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','76','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','98','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','110','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','127','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','168','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','222','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','224','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','297','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','354','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','379','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','417','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','435','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','441','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','474','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','499','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','538','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','548','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','561','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','617','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','625','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','664','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','671','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','768','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','779','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','906','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','914','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','923','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('187','976','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','1','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','10','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','14','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','51','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','102','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','111','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','146','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','206','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','223','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','289','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','311','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','322','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','338','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','396','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','412','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','506','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','517','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','529','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','566','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','593','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','606','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','662','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','770','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','773','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','774','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','815','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','849','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','925','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','988','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('188','989','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','43','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','82','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','171','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','266','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','272','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','315','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','378','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','492','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','509','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','512','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','519','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','533','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','548','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','560','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','628','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','734','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','748','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','788','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','820','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','853','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','882','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','896','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','899','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('189','940','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','38','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','54','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','62','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','87','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','173','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','234','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','253','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','278','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','310','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','374','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','411','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','426','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','472','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','549','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','562','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','606','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','623','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','679','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','682','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','693','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','695','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','705','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','708','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','802','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','806','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','874','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('190','959','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','16','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','39','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','84','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','185','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','219','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','293','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','296','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','378','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','410','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','420','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','461','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','544','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','551','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','596','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','638','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','668','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','692','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','775','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','801','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','819','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','827','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','830','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','834','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','849','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','858','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','914','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','958','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','969','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','971','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('191','993','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','16','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','69','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','117','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','155','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','166','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','179','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','214','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','361','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','367','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','426','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','465','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','470','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','475','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','485','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','541','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','578','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','592','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','614','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','618','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','622','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','674','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','677','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','680','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','682','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','708','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','711','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','747','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','763','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('192','819','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','44','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','80','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','103','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','109','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','119','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','141','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','164','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','291','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','352','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','358','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','376','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','412','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','462','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','689','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','709','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','745','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','807','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','828','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','834','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','851','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','937','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','953','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('193','960','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','9','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','42','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','67','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','86','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','88','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','98','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','135','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','161','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','163','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','215','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','232','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','352','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','415','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','486','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','498','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','531','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','719','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','738','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','786','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','872','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','938','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('194','940','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','129','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','130','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','141','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','144','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','298','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','359','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','361','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','392','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','403','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','494','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','520','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','534','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','560','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','592','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','649','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','658','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','673','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','677','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','706','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','738','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','769','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','781','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','794','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','813','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','869','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','885','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('195','962','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','64','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','122','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','156','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','169','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','276','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','284','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','303','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','324','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','423','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','473','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','484','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','515','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','524','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','541','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','560','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','575','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','576','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','587','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','615','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','635','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','684','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','795','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','815','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','833','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','837','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','906','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','908','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','919','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','939','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('196','972','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','6','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','29','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','63','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','123','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','129','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','147','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','164','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','189','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','243','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','249','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','258','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','364','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','369','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','370','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','418','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','522','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','531','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','554','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','598','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','628','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','691','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','724','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','746','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','752','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','758','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','769','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','815','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','916','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','950','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','967','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','974','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','979','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('197','995','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','1','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','109','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','125','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','186','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','262','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','264','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','303','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','309','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','311','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','329','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','347','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','379','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','395','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','406','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','450','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','464','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','482','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','499','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','536','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','541','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','545','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','555','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','568','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','570','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','588','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','597','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','628','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','745','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','758','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','796','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','806','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','817','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','843','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','858','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','871','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','886','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','892','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','924','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','952','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('198','997','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','67','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','84','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','145','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','159','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','216','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','432','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','541','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','604','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','640','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','689','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','730','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','784','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','785','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','886','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('199','953','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','5','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','49','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','80','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','116','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','121','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','149','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','346','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','419','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','462','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','465','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','474','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','537','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','538','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','544','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','714','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','879','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','912','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','945','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','958','2006-02-15 05:05:03.000')
;
Insert into film_actor
(actor_id,film_id,last_update)
Values
    ('200','993','2006-02-15 05:05:03.000')
;
-- End of Script
--
--
-- Automatically generated by Advanced ETl Processor
-- http://www.etl-tools.com/
-- table film_category
-- Start of script
Insert into film_category
(film_id,category_id,last_update)
Values
    ('1','6','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('2','11','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('3','6','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('4','11','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('5','8','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('6','9','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('7','5','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('8','11','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('9','11','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('10','15','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('11','9','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('12','12','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('13','11','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('14','4','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('15','9','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('16','9','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('17','12','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('18','2','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('19','1','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('20','12','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('21','1','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('22','13','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('23','2','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('24','11','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('25','13','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('26','14','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('27','15','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('28','5','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('29','1','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('30','11','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('31','8','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('32','13','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('33','7','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('34','11','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('35','11','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('36','2','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('37','4','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('38','1','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('39','14','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('40','6','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('41','16','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('42','15','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('43','8','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('44','14','2006-02-15 05:07:09.000')
;
Insert into film_category
(film_id,category_id,last_update)
Values
    ('45','13','2006-02-15 05:07:09.000')
;

-- End of Script
--
--
