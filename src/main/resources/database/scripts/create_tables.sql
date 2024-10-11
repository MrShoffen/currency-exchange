CREATE TABLE IF NOT EXISTS currencies
(
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    code      VARCHAR(3) CHECK ( length(code) == 3 ) UNIQUE NOT NULL,
    full_name VARCHAR(30) UNIQUE                            NOT NULL,
    sign      VARCHAR(10)                                   NOT NULL,
    CONSTRAINT unique_code_name UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS exchange_rates
(
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    base_currency_id   INTEGER NOT NULL REFERENCES currencies (id),
    target_currency_id INTEGER NOT NULL REFERENCES currencies (id),
    rate               REAL    NOT NULL,
    CONSTRAINT unique_exchange_rate UNIQUE (base_currency_id, target_currency_id),
    CONSTRAINT base_not_equals_target CHECK (base_currency_id != target_currency_id)
);
