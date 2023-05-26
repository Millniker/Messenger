CREATE TABLE attachment
(
    id            UUID NOT NULL,
    message_id    UUID,
    attachment_id UUID,
    file_name     VARCHAR(255),
    chat_id       UUID,
    CONSTRAINT pk_attachment PRIMARY KEY (id)
);
CREATE TABLE chat
(
    id     UUID NOT NULL,
    type   VARCHAR(255),
    name   VARCHAR(255),
    admin  UUID,
    date   date,
    avatar UUID,
    CONSTRAINT pk_chat PRIMARY KEY (id)
);

CREATE TABLE chat_message
(
    chat_id    UUID NOT NULL,
    message_id UUID NOT NULL
);

CREATE TABLE message
(
    id           UUID NOT NULL,
    chat_id      UUID,
    send_date    date,
    message_text VARCHAR(255),
    sender_id    UUID,
    CONSTRAINT pk_message PRIMARY KEY (id)
);

CREATE TABLE message_attachments
(
    message_id     UUID NOT NULL,
    attachments_id UUID NOT NULL
);
CREATE TABLE user_chat
(
    id      UUID NOT NULL,
    user_id UUID,
    chat_id UUID,
    CONSTRAINT pk_user_chat PRIMARY KEY (id)
);

ALTER TABLE message_attachments
    ADD CONSTRAINT uc_message_attachments_attachments UNIQUE (attachments_id);

ALTER TABLE message_attachments
    ADD CONSTRAINT fk_mesatt_on_attachment FOREIGN KEY (attachments_id) REFERENCES attachment (id);

ALTER TABLE message_attachments
    ADD CONSTRAINT fk_mesatt_on_message FOREIGN KEY (message_id) REFERENCES message (id);
ALTER TABLE chat_message
    ADD CONSTRAINT uc_chat_message_message UNIQUE (message_id);

ALTER TABLE chat_message
    ADD CONSTRAINT fk_chames_on_chat FOREIGN KEY (chat_id) REFERENCES chat (id);

ALTER TABLE chat_message
    ADD CONSTRAINT fk_chames_on_message FOREIGN KEY (message_id) REFERENCES message (id);