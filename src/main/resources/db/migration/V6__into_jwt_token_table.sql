CREATE TABLE user_tokens
(
    id            VARCHAR(36) PRIMARY KEY,
    refresh_token VARCHAR(1000) NOT NULL,
    email         VARCHAR(255) UNIQUE
);
