--liquibase formatted sql
--changeset rafael:202503090415
--comment: add create_at in cards

ALTER TABLE CARDS ADD COLUMN create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

--rollback ALTER TABLE CARDS ADD COLUMN create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
