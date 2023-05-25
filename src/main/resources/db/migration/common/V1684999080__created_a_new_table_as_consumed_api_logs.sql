CREATE TYPE consumed_api_logs_log_name AS ENUM ('create_receipt', 'get_master_type', 'get_data_for_loan_action', 'cheque_ocr', 's3_upload', 'sms_service', 'shorten_url', 's3_download', 'get_basic_loan_detail', 'get_customer_details', 'get_receipt_date', 'get_token_details', 'global_search', 'get_profile_details', 'fetch_all_user_data', 'contact_support', 'get_pdf', 'razor_pay_ifsc', 'get_user_details_admin');


CREATE table if not EXISTS collection.consumed_api_logs (
    consumed_api_logs_id bigserial NOT NULL,
    created_date timestamptz NOT NULL,
    created_by int8 NOT NULL,
    deleted bool NULL DEFAULT false,
    log_name collection.consumed_api_logs_log_name NULL,
    loan_id int8 NULL,
    api_type varchar(20) NULL,
    request_body json NULL,
    response_data json NULL,
    response_status varchar(50) NULL,
    end_point text NULL,
    CONSTRAINT consumed_api_logs_id_pk PRIMARY KEY (consumed_api_logs_id)
);
CREATE INDEX if not EXISTS consumed_api_logs_created_by_idx ON collection.consumed_api_logs (created_by);
CREATE INDEX if not EXISTS consumed_api_logs_log_name_idx ON collection.consumed_api_logs (log_name);
CREATE INDEX if not EXISTS consumed_api_logs_loan_id_idx ON collection.consumed_api_logs (loan_id);
CREATE INDEX if not EXISTS consumed_api_logs_consumed_api_logs_id_idx ON collection.consumed_api_logs (consumed_api_logs_id);

CREATE sequence if not EXISTS collection.consumed_api_logs_consumed_api_logs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE collection.consumed_api_logs_consumed_api_logs_id_seq OWNED BY collection.consumed_api_logs.consumed_api_logs_id;