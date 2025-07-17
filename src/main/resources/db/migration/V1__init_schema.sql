CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(55) NOT NULL,
    last_name VARCHAR(55) NOT NULL,
    email VARCHAR(70) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(55) NOT NULL,
    last_name VARCHAR(55) NOT NULL,
    email VARCHAR(70) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(55) NOT NULL
                       CHECK ( role IN ('MANAGER', 'ADMIN'))
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL
                    REFERENCES customers(id) ON DELETE RESTRICT,
    status VARCHAR(50) NOT NULL
                    CHECK ( status IN ('NEW','PROCESSING','COMPLETED','CANCELED')),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    price NUMERIC(19, 2)
);

CREATE TABLE product_order (
    product_id BIGINT NOT NULL
                           REFERENCES products(id) ON DELETE RESTRICT,
    order_id BIGINT NOT NULL
                           REFERENCES orders(id) ON DELETE RESTRICT,
    PRIMARY KEY (product_id, order_id)
);
