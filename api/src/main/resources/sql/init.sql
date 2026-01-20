CREATE TABLE brands
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(32) NOT NULL UNIQUE,
    image VARCHAR(256)
);

CREATE TABLE categories
(
    id        SERIAL PRIMARY KEY,
    name      VARCHAR(32) NOT NULL UNIQUE,
    parent_id INT REFERENCES categories (id)
);

CREATE TABLE items
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(256)   NOT NULL,
    brand_id    INT            NOT NULL REFERENCES brands (id) ON DELETE CASCADE,
    category_id INT            NOT NULL REFERENCES categories (id),
    description TEXT,
    price       NUMERIC(10, 2) NOT NULL,
    weight      NUMERIC(10, 3)
);

CREATE TABLE item_images
(
    id      SERIAL PRIMARY KEY,
    item_id INT  NOT NULL REFERENCES items (id) ON DELETE CASCADE,
    image   TEXT NOT NULL,
    is_main BOOLEAN DEFAULT FALSE
);

CREATE TABLE sizes
(
    id        SERIAL PRIMARY KEY,
    name      VARCHAR(8) NOT NULL,
    chest_cm  NUMERIC(5, 2),
    waist_cm  NUMERIC(5, 2),
    length_cm NUMERIC(5, 2)
);

CREATE TABLE item_sizes
(
    id SERIAL PRIMARY KEY,
    item_id INT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    size_id INT NOT NULL REFERENCES sizes(id) ON DELETE CASCADE
);

