INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description, modified_date, modified_by, active, stage)
VALUES
('2024-05-23 13:01:20.000', 1, false, 'is_payment_link_enabled', 'false', 'payment link feature values will be true/false', NULL, NULL, true, NULL),
('2024-05-23 13:01:20.000', 1, false, 'payment_link_vendor_list', '[]', 'keys of vendors will be in array', NULL, NULL, true, NULL);


ALTER TYPE collection.consumed_api_logs_log_name ADD VALUE 'send_payment_link' AFTER 'multi_create_receipt';