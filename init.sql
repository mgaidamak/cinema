-- it's created with docker compose up
-- CREATE USER cinema WITH ENCRYPTED PASSWORD 'cinemapass';
CREATE DATABASE hall;
CREATE DATABASE session;
CREATE DATABASE ticket;
GRANT ALL PRIVILEGES ON DATABASE hall TO cinema;
GRANT ALL PRIVILEGES ON DATABASE session TO cinema;
GRANT ALL PRIVILEGES ON DATABASE ticket TO cinema;