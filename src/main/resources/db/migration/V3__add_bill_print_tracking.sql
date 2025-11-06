-- Create enum for bill print status
CREATE TYPE bill_print_status AS enum (
    'NOT_PRINTED',
    'PRINTED',
    'REPRINTED'
);

-- Add print tracking columns to BILL table (without agent id since multiple agents can print)
ALTER TABLE BILL
ADD COLUMN PRINT_COUNT INT DEFAULT 0,
ADD COLUMN LAST_PRINTED_AT TIMESTAMP,
ADD COLUMN PRINT_STATUS bill_print_status DEFAULT 'NOT_PRINTED';

-- Create BILL_PRINT_HISTORY table to track all print attempts with agent info
CREATE TABLE BILL_PRINT_HISTORY (
    Id VARCHAR(36) PRIMARY KEY,
    Bill_id VARCHAR(36) NOT NULL,
    Print_round INT NOT NULL,
    Printed_at TIMESTAMP NOT NULL,
    Printed_by_agent_id VARCHAR(13) NOT NULL,
    Print_reason VARCHAR(255),

    CONSTRAINT fk_bill_history_id FOREIGN KEY (Bill_id) REFERENCES BILL (Id) ON DELETE CASCADE,
    CONSTRAINT fk_agent_history_id FOREIGN KEY (Printed_by_agent_id) REFERENCES AGENT (Id)
);