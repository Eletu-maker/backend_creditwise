-- Create messages table
CREATE TABLE messages (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    sender_id VARCHAR(36) NOT NULL,
    receiver_id VARCHAR(36) NOT NULL,
    conversation_id VARCHAR(100) NOT NULL,
    message_text TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    message_type VARCHAR(20) NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id)
);

CREATE INDEX idx_message_sender ON messages(sender_id);
CREATE INDEX idx_message_receiver ON messages(receiver_id);
CREATE INDEX idx_message_conversation ON messages(conversation_id);

-- Create appointments table
CREATE TABLE appointments (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    client_id VARCHAR(36) NOT NULL,
    officer_id VARCHAR(36) NOT NULL,
    appointment_datetime TIMESTAMP NOT NULL,
    appointment_type VARCHAR(20) NOT NULL,
    reason TEXT,
    appointment_status VARCHAR(20) NOT NULL,  -- Changed from 'status' to 'appointment_status'
    notes TEXT,
    notified BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (client_id) REFERENCES users(id),
    FOREIGN KEY (officer_id) REFERENCES users(id)
);

CREATE INDEX idx_appointment_client ON appointments(client_id);
CREATE INDEX idx_appointment_officer ON appointments(officer_id);
CREATE INDEX idx_appointment_datetime ON appointments(appointment_datetime);