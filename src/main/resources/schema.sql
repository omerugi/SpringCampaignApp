-- Creating Product table
CREATE TABLE IF NOT EXISTS Product (
    product_serial_number VARCHAR(50) NOT NULL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price REAL NOT NULL,
    active BOOLEAN NOT NULL
    );

-- Creating Campaign table
CREATE TABLE IF NOT EXISTS Campaign (
                                        id BIGSERIAL NOT NULL PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    bid REAL NOT NULL,
    active BOOLEAN NOT NULL
    );

-- Creating Product_Campaign Relationship table
CREATE TABLE IF NOT EXISTS Product_Campaign (
    product_serial_number VARCHAR(50) NOT NULL,
    campaign_id BIGINT NOT NULL,
    PRIMARY KEY (product_serial_number, campaign_id),
    FOREIGN KEY (product_serial_number) REFERENCES Product(product_serial_number) ON DELETE CASCADE,
    FOREIGN KEY (campaign_id) REFERENCES Campaign(id) ON DELETE CASCADE
    );