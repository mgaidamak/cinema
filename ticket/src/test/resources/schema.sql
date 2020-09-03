CREATE TABLE IF NOT EXISTS order (
    id SERIAL PRIMARY KEY,
    customer integer,
    session integer,
    status integer,
    total integer
);

CREATE TABLE IF NOT EXISTS seat (
    id SERIAL PRIMARY KEY,
    order integer REFERENCES order (id) ON DELETE CASCADE,
    session integer NOT NULL CHECK (session > 0),
    seat integer NOT NULL CHECK (seat > 0),
    status integer,
    UNIQUE (session, seat)
);