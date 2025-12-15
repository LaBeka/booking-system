ALTER TABLE user_accounts
    ADD COLUMN auth_provider ENUM ('LOCAL', 'GOOGLE', 'FACEBOOK') NOT NULL DEFAULT 'LOCAL';

UPDATE user_accounts
SET auth_provider = 'LOCAL';