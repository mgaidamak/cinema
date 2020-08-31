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