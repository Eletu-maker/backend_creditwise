-- Insert admin user
INSERT INTO users (id, first_name, last_name, email, password, role, is_enabled, status, created_at, updated_at)
VALUES ('11111111-1111-1111-1111-111111111111', 'Admin', 'User', 'usmaneletu2@gmail.com', 
        '$2a$10$IgYBImb6d5KkPgHncpE90eYkx6zq4.Tx3B5yQrJw8Y.zO6hJ9qFyG', 'ADMIN', TRUE, 'ACTIVE', NOW(), NOW());

-- Insert sample officer
INSERT INTO users (id, first_name, last_name, email, password, role, is_enabled, status, created_at, updated_at)
VALUES ('22222222-2222-2222-2222-222222222222', 'John', 'Officer', 'john.officer@creditwise.com', 
        '$2a$10$8K1p/a0dhrxiowP.dnkgNORTWgdEDHn5L2/xjpEWuC.QQv4rKO9jO', 'OFFICER', TRUE, 'ACTIVE', NOW(), NOW());

-- Insert officer profile
INSERT INTO officer_profiles (id, user_id, max_active_clients, bio, specialization, status, created_at, updated_at)
VALUES ('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', 10, 
        'Experienced credit counselor with 5 years of experience helping clients improve their credit.', 
        'Credit Counseling', 'ACTIVE', NOW(), NOW());

-- Insert sample client
INSERT INTO users (id, first_name, last_name, email, password, role, is_enabled, status, created_at, updated_at)
VALUES ('44444444-4444-4444-4444-444444444444', 'Jane', 'Client', 'jane.client@creditwise.com', 
        '$2a$10$8K1p/a0dhrxiowP.dnkgNORTWgdEDHn5L2/xjpEWuC.QQv4rKO9jO', 'CLIENT', TRUE, 'ACTIVE', NOW(), NOW());

-- Insert client profile
INSERT INTO client_profiles (id, user_id, phone, address, city, state, zip_code, status, created_at, updated_at)
VALUES ('55555555-5555-5555-5555-555555555555', '44444444-4444-4444-4444-444444444444', 
        '555-123-4567', '123 Main St', 'Anytown', 'CA', '12345', 'ACTIVE', NOW(), NOW());

-- Insert sample content
INSERT INTO contents (id, created_by_user_id, title, body, content_type, category, view_count, status, created_at, updated_at)
VALUES ('66666666-6666-6666-6666-666666666666', '11111111-1111-1111-1111-111111111111', 
        'Understanding Credit Scores', 
        'Credit scores are numerical expressions based on a level analysis of a person''s credit files, to represent the creditworthiness of an individual.', 
        'ARTICLE', 'GENERAL_EDUCATION', 0, 'ACTIVE', NOW(), NOW());