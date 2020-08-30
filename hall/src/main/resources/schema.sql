CREATE TABLE cinema (
    id SERIAL,
    name text,
    city text,
    address text,
    timezone text
);

CREATE TABLE hall (
    id SERIAL,
    cinema integer,
    name text
);

CREATE TABLE seat (
    id SERIAL,
    hall integer,
    name text
);