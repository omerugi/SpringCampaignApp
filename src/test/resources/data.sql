-- Inserting data into 'category' table
INSERT INTO category (id, name)
VALUES (111, 'aa'),
       (222, 'bb'),
       (333, 'cc'),
       (444, 'dd');

-- Inserting data into 'product' table
INSERT INTO product (product_serial_number, title, category_id, price, active)
VALUES ('1', 'p1', 111, 20.00, FALSE),
       ('2', 'p2', 222, 22.00, TRUE),
       ('3', 'p3', 333, 23.00, TRUE),
       ('4', 'p4', 111, 23.00, TRUE),
       ('5', 'p5', 222, 23.00, TRUE),
       ('6', 'p6', 333, 23.00, TRUE),
       ('7', 'p7', 222, 16.00, TRUE);

-- Inserting data into 'campaign' table
INSERT INTO campaign (id, name, start_date, bid, active)
VALUES (11, 'camp-test-1', CURRENT_DATE, 400.00, TRUE),
       (22, 'camp-test-2', CURRENT_DATE, 150.00, TRUE),
       (33, 'camp-test-3', CURRENT_DATE, 10000.00, FALSE),
       (44, 'camp-test-4', CURRENT_DATE, 300.00, TRUE);

-- Inserting data into 'product_campaign' table
INSERT INTO product_campaign (product_serial_number, campaign_id)
VALUES ('1', 11),
       ('2', 11),
       ('1', 22),
       ('2', 22),
       ('3', 22),
       ('3', 33),
       ('4', 33),
       ('5', 22),
       ('6', 11),
       ('7', 22),
       ('7', 44);
