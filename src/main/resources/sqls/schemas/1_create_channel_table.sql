CREATE TABLE IF NOT EXISTS channel (
    id BIGSERIAL PRIMARY KEY,
    channel_id VARCHAR(255) UNIQUE NOT NULL,
    host_id_hashed VARCHAR(255) UNIQUE NOT NULL,
    channel_token_id VARCHAR(255) UNIQUE NOT NULL,
    channel_name_enc BYTEA NOT NULL,

    host_channel_token_hashed VARCHAR(255) UNIQUE NOT NULL,
    host_channel_token_enc BYTEA NOT NULL,

    join_channel_token_hashed VARCHAR(255) UNIQUE NOT NULL,
    join_channel_token_enc BYTEA NOT NULL,

    guest_channel_token_hashed VARCHAR(255) UNIQUE NOT NULL,
    guest_channel_token_enc BYTEA NOT NULL,

    secret_key_enc BYTEA,
    -- TODO:
    -- host_public_key TEXT NOT NULL

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

