ALTER TYPE collection.consumed_api_logs_log_name ADD VALUE 'send_qr_code' AFTER 'get_documents';

ALTER TYPE collection.consumed_api_logs_log_name ADD VALUE 'check_qr_payment_status' AFTER 'send_qr_code';

CREATE TABLE IF NOT EXISTS collection.digital_payment_transactions (
    digital_payment_transactions_id bigserial NOT NULL,
    created_date timestamptz NOT NULL,
    created_by int8 NOT NULL,
    modified_date timestamptz NULL,
    modified_by int8 NULL,
    loan_id int8 NOT NULL,
    payment_service_name varchar(50) NOT NULL,
    status varchar(30) NOT NULL,
    amount numeric(20, 2) NOT NULL,
    utr_number varchar(100) NULL,
    receipt_request_body json NOT NULL,
    payment_link text NULL,
    mobile_no int8 NULL,
    vendor varchar(50) NOT NULL,
    receipt_generated bool NULL DEFAULT false,
    collection_activity_logs_id int8 NULL,
    action_activity_logs_id int8 NULL,
    other_response_data json NULL
);

CREATE INDEX digital_payment_transactions_created_by_idx ON collection.digital_payment_transactions (created_by,payment_service_name,status,utr_number,mobile_no,vendor,receipt_generated,collection_activity_logs_id,action_activity_logs_id,loan_id);