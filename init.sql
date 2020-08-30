-- it's created with docker compose up
-- CREATE USER cinema WITH ENCRYPTED PASSWORD 'cinemapass';
CREATE DATABASE hall;
CREATE DATABASE session;
CREATE DATABASE ticket;
GRANT ALL PRIVILEGES ON DATABASE hall TO cinema;
GRANT ALL PRIVILEGES ON DATABASE session TO cinema;
GRANT ALL PRIVILEGES ON DATABASE ticket TO cinema;

\connect hall

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO cinema;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO cinema;

CREATE TABLE IF NOT EXISTS cinema (
id SERIAL,
name text,
city text,
address text,
timezone text
);