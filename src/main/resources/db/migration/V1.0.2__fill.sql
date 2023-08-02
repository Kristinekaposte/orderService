
INSERT INTO orders (order_number, customer_id, order_date, total_price)
VALUES
  ('ORD-123456', 1, '2023-07-20 10:30:00', 1239.92),
  ('ORD-987654', 2, '2023-07-20 11:45:00', 59.97),
  ('ORD-555444', 3, '2023-07-20 13:15:00', 49.96);
INSERT INTO order_item (order_id, product_id, item_price, quantity)
VALUES
  (1, 1, 799.99, 2),
  (1, 2, 19.99, 3),
  (2, 2, 19.99, 1),
  (3, 3, 12.49, 4);