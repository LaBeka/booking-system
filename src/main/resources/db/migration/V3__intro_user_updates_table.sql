CREATE TABLE user_updates
(
    id                  VARCHAR(36) PRIMARY KEY,
    user_id             VARCHAR(36),
    updated_by_admin_id VARCHAR(36),
    updated_at          TIMESTAMP NOT NULL,
    comment             VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES user_accounts (id),
    FOREIGN KEY (updated_by_admin_id) REFERENCES user_accounts (id)
);

insert into user_updates (id, user_id, updated_by_admin_id, updated_at, comment)
VALUES
(UUID(),
    '44444444-4444-4444-4444-444444444444',
    '22222222-2222-2222-2222-222222222222',
    NOW(),
    'some text');