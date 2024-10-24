INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description, modified_date, modified_by, active, stage)
VALUES
(NOW(), 1, true, 'emitra_terms_and_conditions', 'false', 'This configuration is only to provide the terms and conditions for emitra page', NULL, NULL, true, NULL)