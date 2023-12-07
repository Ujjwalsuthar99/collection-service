ALTER TYPE collection.consumed_api_logs_log_name ADD VALUE 'send_otp' AFTER 'send_qr_code';
ALTER TYPE collection.consumed_api_logs_log_name ADD VALUE 'verify_otp' AFTER 'send_otp';