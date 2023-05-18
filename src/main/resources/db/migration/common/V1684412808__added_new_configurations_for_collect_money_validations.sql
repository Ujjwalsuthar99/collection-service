INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description, modified_date, modified_by, active, stage)
VALUES(NOW(), 1, false, 'auto_populate_mobile_number_at_create_receipt', 'false', 'set true to enable to auto populate the customer mobile number or false to disable the same at create receipt', NOW(), NULL, true, NULL);

INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description, modified_date, modified_by, active, stage)
VALUES(NOW(), 1, false, 'duplicate_receipt_time_validation', '10', 'Mention the time in minutes to validate the receipt generate in the past mentioned time', NOW(), NULL, true, NULL);