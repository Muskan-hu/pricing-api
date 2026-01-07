Project Title: Pricing API for Logistics/E-commerce.
Goal: Calculate the delivery price of an item based on distance, weight, and item type.
Tech Stack: Node.js, Express, and sometimes MongoDB/PostgreSQL.
Endpoints:POST /calculate-price: Takes distance, weight, and item_type.
Logic: A formula like: $Price = (Distance \times BaseRate) + (Weight \times Factor)$.
