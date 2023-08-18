DROP SCHEMA IF EXISTS "banking_app" CASCADE;

CREATE SCHEMA "banking_app";

DROP TABLE IF EXISTS banking_app.customer CASCADE;

CREATE TABLE banking_app.customer
(
    id   serial PRIMARY KEY,
    name character varying NOT NULL
);

INSERT INTO banking_app.customer
VALUES (1, 'Arisha Barron'),
       (2, 'Branden Gibson'),
       (3, 'Rhonda Church'),
       (4, 'Georgina Hazel');