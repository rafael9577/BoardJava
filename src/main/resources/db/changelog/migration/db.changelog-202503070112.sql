--liquibase formatted sql
--changeset rafael:202503061959
--comment: blocks table create

CREATE TABLE BLOCKS(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    block_reason VARCHAR(255) NOT NULL,
    unblocked_at TIMESTAMP NULL,
    unblock_reason VARCHAR(255) NOT NULL,
    cards_id BIGINT NOT NULL,
    CONSTRAINT cards__blocks_fk FOREIGN KEY (cards_id) REFERENCES CARDS(id) ON DELETE CASCADE
) ENGINE = InnoDB;

--rollback DROP TABLE BLOCKS