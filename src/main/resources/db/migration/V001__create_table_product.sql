create table product(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    quantity INT,
    price DECIMAL(5, 2),
    description VARCHAR(255),
    image VARCHAR(255),
    for_animal VARCHAR(255),
    utility varchar(255)
);