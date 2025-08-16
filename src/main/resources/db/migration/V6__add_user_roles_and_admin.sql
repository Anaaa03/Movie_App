ALTER TABLE users
    ADD COLUMN role TEXT NOT NULL DEFAULT 'USER';

INSERT INTO users (id, username, email, password_hash, role, created_at)
VALUES ('550e8400-e29b-41d4-a716-446655440000',
        'admin',
        'admin@movieapp.com',
        '$2a$10$A9J6gvuiFNX1zqqNdobw4eQyNslwbJ71mDuKzqiKNEDKJyt.Qt6mW', -- password: admin123
        'ADMIN',
        NOW());