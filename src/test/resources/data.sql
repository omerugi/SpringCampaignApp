-- Inserting data into Product table
INSERT INTO Product (product_serial_number, title, category, price, active)
VALUES ('1', 'p1', 'aa', 20.00, FALSE),
       ('2', 'p2', 'bb', 22.00, TRUE),
       ('3', 'p3', 'cc', 23.00, TRUE),
       ('4', 'p4', 'aa', 23.00, TRUE),
       ('5', 'p5', 'bb', 23.00, TRUE),
       ('6', 'p6', 'cc', 23.00, TRUE),
       ('7', 'p7', 'bb', 23.00, TRUE);

-- Inserting data into Campaign table
INSERT INTO Campaign (id, name, start_date, bid, active)
VALUES (11, 'camp-test-1', CURRENT_DATE, 400.00, TRUE),
       (22, 'camp-test-2', CURRENT_DATE, 150.00, TRUE),
       (33, 'camp-test-3', CURRENT_DATE, 10000.00, FALSE);

-- Inserting data into Product_Campaign table
INSERT INTO Product_Campaign (product_serial_number, campaign_id)
VALUES ('1', 11),
       ('2', 11),
       ('1', 22),
       ('2', 22),
       ('3', 22),
       ('3', 33),
       ('4', 33),
       ('5', 22),
       ('6', 11),
       ('7', 22);
