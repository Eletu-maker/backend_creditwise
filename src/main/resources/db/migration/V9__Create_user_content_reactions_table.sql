-- Create user_content_reactions table for tracking user reactions to content
CREATE TABLE user_content_reactions (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    content_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    reaction_type VARCHAR(10) NOT NULL,
    FOREIGN KEY (content_id) REFERENCES contents(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_content_user_reaction UNIQUE (content_id, user_id)
);

CREATE INDEX idx_reaction_content ON user_content_reactions(content_id);
CREATE INDEX idx_reaction_user ON user_content_reactions(user_id);
CREATE INDEX idx_reaction_type ON user_content_reactions(reaction_type);