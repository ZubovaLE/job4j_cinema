CREATE TABLE accounts
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR NOT NULL,
    phone    VARCHAR NOT NULL UNIQUE,
    email    VARCHAR UNIQUE
);

CREATE TABLE seats
(
    id       SERIAL PRIMARY KEY,
    row      INT NOT NULL,
    cell     INT NOT NULL,
    bought BOOLEAN
);

INSERT INTO seats(row, cell, bought)
VALUES (1, 1, TRUE),
       (1, 2, TRUE),
       (1, 3, TRUE),
       (2, 1, TRUE),
       (2, 2, TRUE),
       (2, 3, TRUE),
       (3, 1, TRUE),
       (3, 2, TRUE),
       (3, 3, TRUE);

CREATE TABLE tickets
(
    id         SERIAL PRIMARY KEY,
    seat_id    INT NOT NULL REFERENCES seats (id),
    account_id INT NOT NULL REFERENCES accounts (id),
    session_id INT NOT NULL
);