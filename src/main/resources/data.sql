DROP TABLE IF EXISTS customer;
CREATE TABLE customer (
  id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
  name VARCHAR(255),
  city VARCHAR(255),
  age INT
);
INSERT INTO customer ( name, city, age) VALUES ( 'Alice', 'New York', 31);
INSERT INTO customer (name, city, age) VALUES ( 'Bob', 'Los Angeles', 38);
INSERT INTO customer ( name, city, age) VALUES ('Charlie', 'India', 40);