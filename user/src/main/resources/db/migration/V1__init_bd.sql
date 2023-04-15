CREATE TABLE users
(
    id                UUID         NOT NULL,
    first_name        VARCHAR(255),
    second_name       VARCHAR(255),
    patronymic        VARCHAR(255),
    birth_date        date,
    password          VARCHAR(255) NOT NULL,
    email             VARCHAR(255) NOT NULL,
    number            VARCHAR(255),
    avatar            UUID,
    city              VARCHAR(255),
    registration_date date,
    login             VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_login UNIQUE (login);