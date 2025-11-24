CREATE TABLE roles
(
    id   BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);
CREATE TABLE user_accounts
(
    id         VARCHAR(36) PRIMARY KEY,
    full_name  VARCHAR(255),
    email      VARCHAR(255) UNIQUE,
    password   VARCHAR(255)          NOT NULL,
    active     BOOLEAN DEFAULT TRUE  NOT NULL,
    deprecated BOOLEAN DEFAULT FALSE NOT NULL
);
CREATE TABLE users_roles
(
    user_id VARCHAR(36) NOT NULL,
    role_id BIGINT      NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user_accounts (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);
CREATE TABLE events
(
    id                   VARCHAR(36) PRIMARY KEY,
    title                VARCHAR(255)          NOT NULL,
    description          TEXT,
    type                 VARCHAR(50)           NOT NULL, -- ENUM stored as string
    active               BOOLEAN DEFAULT TRUE  NOT NULL,
    deprecated           BOOLEAN DEFAULT FALSE NOT NULL,
    location             VARCHAR(255),
    when_time            TIMESTAMP,                      -- LocalDateTime
    created_by_admin_id  VARCHAR(36)           NOT NULL,
    created_at           TIMESTAMP,
    max_participants     INT,
    current_participants INT     DEFAULT 0,
    FOREIGN KEY (created_by_admin_id) REFERENCES user_accounts (id)
);
CREATE TABLE bookings
(
    id        VARCHAR(36) PRIMARY KEY,
    booked_by VARCHAR(36)          NOT NULL,
    event_id  VARCHAR(36)          NOT NULL,
    booked_on TIMESTAMP            NOT NULL,
    active    BOOLEAN DEFAULT TRUE NOT NULL,
    FOREIGN KEY (booked_by) REFERENCES user_accounts (id),
    FOREIGN KEY (event_id) REFERENCES events (id)
);
