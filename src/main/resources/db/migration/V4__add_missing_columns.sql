ALTER TABLE transparency_record
    RENAME COLUMN timestamp TO record_timestamp;

ALTER TABLE transparency_record
    ADD COLUMN provider VARCHAR(50),
    ADD COLUMN severity VARCHAR(20),
    ADD COLUMN details TEXT,
    ADD COLUMN event_timestamp TIMESTAMP;
