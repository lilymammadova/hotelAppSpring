CREATE TABLE IF NOT EXISTS apartments
(
    id        INT NOT NULL AUTO_INCREMENT,
    price     DOUBLE,
    status    VARCHAR(45),
    client_id INT,
    PRIMARY KEY (id),
    KEY fk_client (client_id),
    CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES clients (id) ON DELETE SET NULL
);