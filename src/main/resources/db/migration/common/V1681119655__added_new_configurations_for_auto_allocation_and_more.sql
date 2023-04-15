INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description)
VALUES('2023-01-27 13:01:20.000', 1, false, 'receipt_transfer_default_mode', 'bank', 'We can set the default value of receipt transfer mode while proceeding the transfer.');
INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description)
VALUES('2023-01-27 13:01:20.000', 1, false, 'auto_allocation_default_mode', 'No', 'We can set the default value of auto allocation by mentioning Yes/No in the configuration value column for the same.');
INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description)
VALUES('2023-01-27 13:01:20.000', 1, false, 'auto_allocation_read_only', 'true', 'We can enable/disable the radio button for auto allocation.');
INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description)
VALUES('2023-01-27 13:01:20.000', 1, false, 'receipt_transfer_mode_read_only', 'false', 'We can enable/disable the modes of receipt transfer');
INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description)
VALUES('2023-02-24 11:36:24.757', 1, false, 'online_collection_default_limit', '400000', 'Collection limit for any UPI/NEFT/wallet transactions.');
INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description)
VALUES('2023-02-24 11:36:24.757', 1, false, 'add_contact_popup_at_create_receipt', 'true', 'If a client want to use the add contact feature at the time of receipt generate then we can set this true or false.');
INSERT INTO collection.collection_configurations (created_date,created_by,deleted,configuration_name,configuration_value,configuration_description)
VALUES('2023-02-24 11:36:24.757', 1, false, 'mask_all_customer_phone_numbers', 'true', 'We can set masking of loan participant phone numbers masking enable/disable with this configuration');
