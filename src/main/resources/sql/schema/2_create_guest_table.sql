CREATE TABLE IF NOT EXISTS guest (
    id BIGSERIAL PRIMARY KEY,    
    guest_id_hashed VARCHAR(255) UNIQUE,
    guest_id_enc BYTEA,


    is_authenticated BOOLEAN,

    visitor_id_hashed VARCHAR(255) UNIQUE NOT NULL,
    visitor_id_enc BYTEA NOT NULL,

    channel_id VARCHAR(255) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

);