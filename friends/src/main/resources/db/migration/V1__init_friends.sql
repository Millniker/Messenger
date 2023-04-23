CREATE TABLE friend
(
    id              UUID NOT NULL,
    add_time        TIMESTAMP WITHOUT TIME ZONE,
    delete_time     TIMESTAMP WITHOUT TIME ZONE,
    main_id         VARCHAR(255),
    added_friend_id VARCHAR(255),
    first_name      VARCHAR(255),
    second_name     VARCHAR(255),
    patronymic      VARCHAR(255),
    deleted         VARCHAR(255),
    CONSTRAINT pk_friend PRIMARY KEY (id)
);