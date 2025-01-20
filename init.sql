CREATE TABLE carts (
    id INT PRIMARY KEY,
    total_in_cents INT NOT NULL
);

CREATE TABLE cart_items (
    id INT PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    recipe_id INT NULL
);

CREATE TABLE products (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price_in_cents INT NOT NULL
);

CREATE TABLE recipes (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE recipe_ingredients (
    id INT PRIMARY KEY,
    recipe_id INT NOT NULL,
    product_id INT NOT NULL
);

ALTER TABLE cart_items
  ADD CONSTRAINT fk_cart_id FOREIGN KEY (cart_id) REFERENCES carts(id),
  ADD CONSTRAINT fk_product_id FOREIGN KEY (product_id) REFERENCES products(id);

ALTER TABLE recipe_ingredients
  ADD CONSTRAINT fk_recipe_id FOREIGN KEY (recipe_id) REFERENCES recipes(id),
  ADD CONSTRAINT fk_product_id_recipe_ingredient FOREIGN KEY (product_id) REFERENCES products(id);


-- Insert a cart
INSERT INTO carts (id, total_in_cents) VALUES (1, 0);

-- Insert some products
INSERT INTO products (id, name, price_in_cents) VALUES (10, 'Tomato', 250);
INSERT INTO products (id, name, price_in_cents) VALUES (11, 'Onion', 150);

-- Insert a recipe
INSERT INTO recipes (id, name) VALUES (99, 'Tomato Soup');

-- Link products to the recipe
INSERT INTO recipe_ingredients (id, recipe_id, product_id) VALUES (1, 99, 10);
INSERT INTO recipe_ingredients (id, recipe_id, product_id) VALUES (2, 99, 11);
