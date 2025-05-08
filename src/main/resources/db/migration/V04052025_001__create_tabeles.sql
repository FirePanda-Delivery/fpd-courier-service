CREATE SEQUENCE IF NOT EXISTS courier_id_sequence START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS location_id_sequence START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS order_id_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE courier
(
    id         BIGINT       NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    phone      VARCHAR(255) NOT NULL,
    email      VARCHAR(255),
    rating     FLOAT,
    status     VARCHAR(255),
    city       VARCHAR(255) NOT NULL,
    is_deleted BOOLEAN,
    CONSTRAINT pk_courier PRIMARY KEY (id)
);

CREATE TABLE location
(
    id         BIGINT NOT NULL,
    courier_id BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    order_id   BIGINT,
    x          DOUBLE PRECISION,
    y          DOUBLE PRECISION,
    CONSTRAINT pk_location PRIMARY KEY (id)
);

CREATE TABLE "order"
(
    id                 BIGINT                      NOT NULL,
    order_id           BIGINT                      NOT NULL,
    courier_id         BIGINT,
    address            VARCHAR(255)                NOT NULL,
    restaurant_address VARCHAR(255)                NOT NULL,
    city               VARCHAR(255)                NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    on_active          BOOLEAN                     NOT NULL,
    CONSTRAINT pk_order PRIMARY KEY (id)
);

ALTER TABLE courier
    ADD CONSTRAINT uc_courier_email UNIQUE (email);

ALTER TABLE courier
    ADD CONSTRAINT uc_courier_phone UNIQUE (phone);

ALTER TABLE "order"
    ADD CONSTRAINT uc_order_order_id UNIQUE (order_id);

ALTER TABLE location
    ADD CONSTRAINT FK_LOCATION_ON_COURIER FOREIGN KEY (courier_id) REFERENCES courier (id);

ALTER TABLE "order"
    ADD CONSTRAINT FK_ORDER_ON_COURIER FOREIGN KEY (courier_id) REFERENCES courier (id);