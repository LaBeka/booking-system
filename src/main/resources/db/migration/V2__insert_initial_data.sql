INSERT INTO roles (id, name)
VALUES (1, 'SUPER_ADMIN'),
       (2, 'ADMIN'),
       (3, 'USER');

INSERT INTO user_accounts (id, full_name, email, password, active, deprecated)
VALUES ('11111111-1111-1111-1111-111111111111', 'Admin One', 'admin1@school.com', 'pass', TRUE, FALSE),
       ('22222222-2222-2222-2222-222222222222', 'Admin Two', 'admin2@school.com', 'pass', TRUE, FALSE),
       ('33333333-3333-3333-3333-333333333333', 'Admin Three', 'admin3@school.com', 'pass', TRUE, FALSE),
       ('44444444-4444-4444-4444-444444444444', 'Student A', 'a@student.com', 'pass', TRUE, FALSE),
       ('55555555-5555-5555-5555-555555555555', 'Student B', 'b@student.com', 'pass', TRUE, FALSE),
       ('66666666-6666-6666-6666-666666666666', 'Student C', 'c@student.com', 'pass', TRUE, FALSE),
       ('77777777-7777-7777-7777-777777777777', 'Student D', 'd@student.com', 'pass', TRUE, FALSE),
       ('88888888-8888-8888-8888-888888888888', 'Student E', 'e@student.com', 'pass', TRUE, FALSE),
       ('99999999-9999-9999-9999-999999999999', 'Alice Brown', 'alice@school.com', 'pass', TRUE, FALSE),
       ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Bob Smith', 'bob@school.com', 'pass', TRUE, FALSE),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Carol Lee', 'carol@school.com', 'pass', TRUE, FALSE),
       ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Daniel King', 'daniel@school.com', 'pass', TRUE, FALSE),
       ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Eve Stone', 'eve@school.com', 'pass', TRUE, FALSE);

INSERT INTO users_roles
VALUES ('11111111-1111-1111-1111-111111111111', 1),
       ('11111111-1111-1111-1111-111111111111', 2),
       ('11111111-1111-1111-1111-111111111111', 3);

-- Admin Two
INSERT INTO users_roles
VALUES ('22222222-2222-2222-2222-222222222222', 2),
       ('22222222-2222-2222-2222-222222222222', 3);

-- Admin Three
INSERT INTO users_roles
VALUES ('33333333-3333-3333-3333-333333333333', 2),
       ('33333333-3333-3333-3333-333333333333', 3);

-- Students → USER
INSERT INTO users_roles
VALUES ('44444444-4444-4444-4444-444444444444', 3),
       ('55555555-5555-5555-5555-555555555555', 3),
       ('66666666-6666-6666-6666-666666666666', 3),
       ('77777777-7777-7777-7777-777777777777', 3),
       ('88888888-8888-8888-8888-888888888888', 3),
       ('99999999-9999-9999-9999-999999999999', 3),
       ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 3),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 3),
       ('cccccccc-cccc-cccc-cccc-cccccccccccc', 3),
       ('dddddddd-dddd-dddd-dddd-dddddddddddd', 3);

INSERT INTO events
(id, title, description, type, active, deprecated, location, when_time,
 created_by_admin_id, created_at, max_participants, current_participants)
VALUES
    ('eeeeeeee-1111-1111-1111-111111111111',
     'Imagine Dragons Concert',
     'Live performance in Stockholm Arena',
     'CONCERT',
     TRUE, FALSE,
     'Stockholm Arena',
     '2026-02-20 19:00:00',
     '11111111-1111-1111-1111-111111111111',
     '2025-10-20 19:00:00',
     10, 0),

    ('eeeeeeee-2222-2222-2222-222222222222',
     'REDBULL Masterclass SKIING',
     'Intensive training',
     'SPORT_EVENT',
     TRUE, FALSE,
     'Åre Ski Campus',
     '2026-01-10 10:00:00',
     '22222222-2222-2222-2222-222222222222',
     '2025-09-20 19:00:00',
     5, 0),

    ('eeeeeeee-3333-3333-3333-333333333333',
     'Java Spring Boot Masterclass',
     'Intensive backend engineering course',
     'COURSE',
     TRUE, FALSE,
     'KTH Campus',
     '2026-03-10 10:00:00',
     '33333333-3333-3333-3333-333333333333',
     '2025-10-20 19:00:00',
     5, 0);

INSERT INTO bookings (id, booked_by, event_id, booked_on, active)
VALUES
    ('ffffffff-1111-1111-1111-111111111111',
     '44444444-4444-4444-4444-444444444444',
     'eeeeeeee-3333-3333-3333-333333333333',
     NOW(),
     TRUE);