-- Schema for 'campaign' table
CREATE TABLE IF NOT EXISTS campaign (
                                        id SERIAL PRIMARY KEY,
                                        name VARCHAR(25) NOT NULL CHECK (LENGTH(name) > 1),
    start_date DATE NOT NULL,
    bid DECIMAL CHECK (bid >= 0),
    active BOOLEAN DEFAULT TRUE
    );

-- Schema for 'category' table
CREATE TABLE IF NOT EXISTS category (
                                        id SERIAL PRIMARY KEY,
                                        name VARCHAR(25) UNIQUE NOT NULL CHECK (LENGTH(name) > 1)
    );

-- Schema for 'product' table
CREATE TABLE IF NOT EXISTS product (
    product_serial_number VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(25) UNIQUE NOT NULL CHECK (LENGTH(title) > 1),
    price DECIMAL NOT NULL CHECK (price >= 0),
    active BOOLEAN DEFAULT TRUE,
    category_id INTEGER NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(id)
    );

-- Schema for 'product_campaign' table (junction table for many-to-many relationship)
CREATE TABLE IF NOT EXISTS product_campaign (
    product_serial_number VARCHAR(255),
    campaign_id INTEGER,
    PRIMARY KEY (product_serial_number, campaign_id),
    FOREIGN KEY (product_serial_number) REFERENCES product(product_serial_number),
    FOREIGN KEY (campaign_id) REFERENCES campaign(id)
    );
