DROP SCHEMA IF EXISTS "banking_app" CASCADE;

CREATE SCHEMA "banking_app";

DROP TABLE IF EXISTS banking_app.customer CASCADE;

CREATE TABLE banking_app.customer
(
    id   serial PRIMARY KEY,
    name character varying NOT NULL
);