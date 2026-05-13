ALTER TABLE transparency_record
    ADD COLUMN risk_score INT,
    ADD COLUMN algorithm_version VARCHAR(64);