DROP SCHEMA IF EXISTS "banking_app" CASCADE;

CREATE SCHEMA "banking_app";

DROP TABLE IF EXISTS banking_app.customer CASCADE;

CREATE TABLE banking_app.customer
(
    id   serial PRIMARY KEY,
    name character varying NOT NULL
);

INSERT INTO banking_app.customer(name)
VALUES ('Arisha Barron'),
       ('Branden Gibson'),
       ('Rhonda Church'),
       ('Georgina Hazel');