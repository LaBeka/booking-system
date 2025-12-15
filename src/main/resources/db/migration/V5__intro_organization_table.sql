-- Create organizations table
CREATE TABLE organizations (
                               id VARCHAR(36) PRIMARY KEY,
                               name VARCHAR(255) NOT NULL,
                               email VARCHAR(255) UNIQUE,
                               active BOOLEAN DEFAULT TRUE,
                               deprecated BOOLEAN DEFAULT FALSE
);

INSERT INTO organizations (id, name, email)
VALUES ('11111111-1111-1111-1111-111111111111', 'Lexicon Academy', 'contact@lexicon.dev'),
       ('22222222-2222-2222-2222-222222222222', 'Stockholm Tech Hub', 'info@sth-techhub.dev'),
       ('33333333-3333-3333-3333-333333333333', 'Global Lovable Events Group', 'love@glovable.exe');
ALTER TABLE events
    ADD COLUMN org_id VARCHAR(36);

UPDATE events
SET org_id = '11111111-1111-1111-1111-111111111111'
WHERE org_id IS NULL;

ALTER TABLE events
    MODIFY COLUMN org_id VARCHAR(36) NOT NULL,
    ADD CONSTRAINT fk_event_org
        FOREIGN KEY (org_id)
            REFERENCES organizations(id);

-- Add org_id to user_accounts (nullable: a user may be global / not tied yet)
ALTER TABLE user_accounts
    ADD COLUMN org_id VARCHAR(36) NULL,
    ADD CONSTRAINT fk_user_accounts_org
        FOREIGN KEY (org_id) REFERENCES organizations (id);