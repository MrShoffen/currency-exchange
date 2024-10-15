INSERT INTO currencies (code, full_name, sign)
VALUES ('USD', 'US Dollar', '$'),
       ('EUR', 'Euro', '€'),
       ('GBP', 'Pound Sterling', '£'),
       ('CNY', 'Yuan Renminbi', '¥'),
       ('JPY', 'Yen', '¥');

INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
VALUES (1, 2, 0.9),
       (1, 3, 0.75),
       (1,5,144.42);