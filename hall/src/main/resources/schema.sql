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