CREATE TABLE transparency_record (
    id SERIAL PRIMARY KEY,
    election_id BIGINT,
    event_type VARCHAR(100),
    description TEXT,
    timestamp TIMESTAMP
);