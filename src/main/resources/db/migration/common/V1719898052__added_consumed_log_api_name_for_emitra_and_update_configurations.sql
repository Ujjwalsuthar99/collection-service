ALTER TYPE collection.consumed_api_logs_log_name ADD VALUE 'emitra_verify_sso_token' AFTER 'check_payment_link_status';
ALTER TYPE collection.consumed_api_logs_log_name ADD VALUE 'emitra_verify_transaction' AFTER 'emitra_verify_sso_token';
ALTER TYPE collection.consumed_api_logs_log_name ADD VALUE 'emitra_get_kiosk_details' AFTER 'emitra_verify_transaction';
ALTER TYPE collection.consumed_api_logs_log_name ADD VALUE 'emitra_service_transaction' AFTER 'emitra_get_kiosk_details';
ALTER TYPE collection.consumed_api_logs_log_name ADD VALUE 'emitra_update_transaction_posting' AFTER 'emitra_service_transaction';