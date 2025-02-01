CREATE TABLE users
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL
);

CREATE TABLE categories
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id VARCHAR(255) NOT NULL
);

CREATE TABLE attendance
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    status      VARCHAR(255) NOT NULL,
    email_sent  BOOLEAN      NOT NULL,
    created_at  DATETIME     NOT NULL,
    user_id     BIGINT       NOT NULL,
    category_id BIGINT       NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories (id)
);