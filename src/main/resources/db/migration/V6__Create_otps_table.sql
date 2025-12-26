-- Create otps table
CREATE TABLE otps (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    email VARCHAR(100) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_otp_email ON otps(email);
CREATE INDEX idx_otp_code ON otps(otp_code);
CREATE INDEX idx_otp_expires_at ON otps(expires_at);