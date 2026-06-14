CREATE TABLE transactions (
    id               BIGSERIAL       PRIMARY KEY,
    category_id      BIGINT          NOT NULL REFERENCES categories(id),
    amount           NUMERIC(15, 2)  NOT NULL,
    transaction_date DATE            NOT NULL,
    description      VARCHAR(255),
    external_id      VARCHAR(255),
    user_id          BIGINT,
    created_at       TIMESTAMP       NOT NULL,
    updated_at       TIMESTAMP       NOT NULL,

    CONSTRAINT uq_transaction_user_external_id UNIQUE (user_id, external_id)
);
