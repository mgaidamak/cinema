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
    id SERIAL PRIMARY KEY,
    name text,
    city text,
    address text,
    timezone text
);

CREATE TABLE IF NOT EXISTS hall (
    id SERIAL PRIMARY KEY,
    cinema integer REFERENCES cinema (id) ON DELETE CASCADE,
    name text
);

CREATE TABLE IF NOT EXISTS seat (
    id SERIAL PRIMARY KEY,
    hall integer REFERENCES hall (id) ON DELETE CASCADE,
    x integer NOT NULL CHECK (x > 0),
    y integer NOT NULL CHECK (y > 0),
    UNIQUE (hall, x, y)
);

\connect session

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO cinema;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO cinema;

CREATE TABLE IF NOT EXISTS film (
    id SERIAL PRIMARY KEY,
    name text
);

CREATE TABLE IF NOT EXISTS session (
    id SERIAL PRIMARY KEY,
    film integer REFERENCES film (id) ON DELETE CASCADE,
    hall integer,
    date timestamp with time zone,
    price integer
);

\connect ticket

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO cinema;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO cinema;

CREATE TABLE IF NOT EXISTS bill (
    id SERIAL PRIMARY KEY,
    customer integer,
    session integer,
    status integer,
    total integer
);

CREATE TABLE IF NOT EXISTS seat (
    id SERIAL PRIMARY KEY,
    bill integer REFERENCES bill (id) ON DELETE CASCADE,
    session integer NOT NULL CHECK (session > 0),
    seat integer NOT NULL CHECK (seat > 0),
    status integer,
    UNIQUE (session, seat)
);