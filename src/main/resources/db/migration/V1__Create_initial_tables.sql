-- Create users table
CREATE TABLE users (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    is_enabled BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_role ON users(role);

-- Create officer_profiles table
CREATE TABLE officer_profiles (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    user_id VARCHAR(36) NOT NULL,
    max_active_clients INT NOT NULL,
    bio VARCHAR(500),
    specialization VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_officer_user ON officer_profiles(user_id);

-- Create client_profiles table
CREATE TABLE client_profiles (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    user_id VARCHAR(36) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(200),
    city VARCHAR(50),
    state VARCHAR(50),
    zip_code VARCHAR(10),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_client_user ON client_profiles(user_id);

-- Create officer_client_assignments table
CREATE TABLE officer_client_assignments (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    officer_id VARCHAR(36) NOT NULL,
    client_id VARCHAR(36) NOT NULL,
    assignment_status VARCHAR(20) NOT NULL,
    FOREIGN KEY (officer_id) REFERENCES users(id),
    FOREIGN KEY (client_id) REFERENCES users(id)
);

CREATE INDEX idx_assignment_officer ON officer_client_assignments(officer_id);
CREATE INDEX idx_assignment_client ON officer_client_assignments(client_id);
CREATE INDEX idx_assignment_status ON officer_client_assignments(assignment_status);

-- Create credit_health_scores table
CREATE TABLE credit_health_scores (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    client_id VARCHAR(36) NOT NULL,
    score_date DATE NOT NULL,
    score INT NOT NULL,
    payment_history_score INT,
    credit_utilization_score INT,
    credit_age_score INT,
    credit_mix_score INT,
    recent_activity_score INT,
    disclaimer VARCHAR(200),
    FOREIGN KEY (client_id) REFERENCES users(id)
);

CREATE INDEX idx_score_client ON credit_health_scores(client_id);
CREATE INDEX idx_score_date ON credit_health_scores(score_date);

-- Create uploaded_credit_reports table
CREATE TABLE uploaded_credit_reports (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    client_id VARCHAR(36) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    upload_date TIMESTAMP NOT NULL,
    content_type VARCHAR(100),
    summary VARCHAR(1000),
    issues TEXT,
    recommendations TEXT,
    is_expired BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (client_id) REFERENCES users(id)
);

CREATE INDEX idx_report_client ON uploaded_credit_reports(client_id);
CREATE INDEX idx_report_upload_date ON uploaded_credit_reports(upload_date);

-- Create credit_plans table
CREATE TABLE credit_plans (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    client_id VARCHAR(36) NOT NULL,
    officer_id VARCHAR(36) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    plan_status VARCHAR(20),
    FOREIGN KEY (client_id) REFERENCES users(id),
    FOREIGN KEY (officer_id) REFERENCES users(id)
);

CREATE INDEX idx_plan_client ON credit_plans(client_id);
CREATE INDEX idx_plan_officer ON credit_plans(officer_id);

-- Create plan_tasks table
CREATE TABLE plan_tasks (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    plan_id VARCHAR(36) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    due_date DATE,
    task_status VARCHAR(20),
    FOREIGN KEY (plan_id) REFERENCES credit_plans(id)
);

CREATE INDEX idx_task_plan ON plan_tasks(plan_id);
CREATE INDEX idx_task_status ON plan_tasks(task_status);

-- Create contents table
CREATE TABLE contents (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_by_user_id VARCHAR(36) NOT NULL,
    title VARCHAR(200) NOT NULL,
    body TEXT,
    content_type VARCHAR(20) NOT NULL,
    category VARCHAR(50),
    view_count INT DEFAULT 0,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

CREATE INDEX idx_content_category ON contents(category);
CREATE INDEX idx_content_created_by ON contents(created_by_user_id);

-- Create content_likes table
CREATE TABLE content_likes (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    content_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    FOREIGN KEY (content_id) REFERENCES contents(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_content_user UNIQUE (content_id, user_id)
);

CREATE INDEX idx_like_content ON content_likes(content_id);
CREATE INDEX idx_like_user ON content_likes(user_id);

-- Create comments table
CREATE TABLE comments (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    content_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    comment_text TEXT NOT NULL,
    FOREIGN KEY (content_id) REFERENCES contents(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_comment_content ON comments(content_id);
CREATE INDEX idx_comment_user ON comments(user_id);