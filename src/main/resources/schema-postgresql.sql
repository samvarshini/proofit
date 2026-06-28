CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS documents (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50),
    file_size BIGINT,
    document_hash VARCHAR(64) NOT NULL,
    chain_hash VARCHAR(64) NOT NULL,
    previous_chain_hash VARCHAR(64),
    verification_token VARCHAR(100) UNIQUE,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
