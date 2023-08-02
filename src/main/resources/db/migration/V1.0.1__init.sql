use orderdb;

DROP TABLE IF EXISTS  order_item;
DROP TABLE IF EXISTS  orders;

CREATE TABLE orders (
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  order_number VARCHAR(40) NOT NULL UNIQUE,
  customer_id BIGINT NOT NULL,
  order_date DATETIME NOT NULL,
  total_price DOUBLE NOT NULL
);
CREATE TABLE order_item (
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  item_price DOUBLE NOT NULL,
  quantity INTEGER NOT NULL,
  FOREIGN KEY (order_id) REFERENCES orders(id)
);



