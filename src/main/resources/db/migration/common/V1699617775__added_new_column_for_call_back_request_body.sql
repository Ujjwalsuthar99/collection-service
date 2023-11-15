drop table if exists collection.digital_payment_transactions;

CREATE TABLE IF NOT EXISTS collection.digital_payment_transactions (
    digital_payment_trans_id bigserial NOT NULL,
    created_date timestamptz NOT NULL,
    created_by int8 NOT NULL,
    modified_date timestamptz NULL,
    modified_by int8 NULL,
    loan_id int8 NOT NULL,
    payment_service_name varchar(50) NOT NULL,
    status varchar(30) NOT NULL,
    merchant_tran_id varchar(50) NOT NULL,
    amount numeric(20, 2) NOT NULL,
    utr_number varchar(100) NULL,
    receipt_request_body json NOT NULL,
    payment_link text NULL,
    mobile_no int8 NULL,
    vendor varchar(50) NOT NULL,
    receipt_generated bool NULL DEFAULT false,
    collection_activity_logs_id int8 NULL,
    action_activity_logs_id int8 NULL,
    other_response_data json NULL,
    call_back_request_body NULL,
    CONSTRAINT digital_payment_trans_id_pk PRIMARY KEY (digital_payment_trans_id)
);

CREATE sequence if not EXISTS collection.digital_payment_transactions_digital_payment_trans_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE collection.digital_payment_transactions_digital_payment_trans_id_seq OWNED BY collection.digital_payment_transactions.digital_payment_trans_id;