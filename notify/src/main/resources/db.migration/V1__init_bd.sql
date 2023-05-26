CREATE TABLE notif
(
    id          UUID NOT NULL,
    type        VARCHAR(255),
    text        VARCHAR(255),
    user_id     UUID,
    read_status VARCHAR(255),
    readed_time TIMESTAMP WITHOUT TIME ZONE,
    send_date   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_notif PRIMARY KEY (id)
);