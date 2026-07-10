CREATE TABLE users (
    id             UUID PRIMARY KEY,
    email          VARCHAR(255) NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    role           VARCHAR(50)  NOT NULL,
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    registered_at  TIMESTAMP    NOT NULL
);

CREATE UNIQUE INDEX uk_users_email ON users (email);
