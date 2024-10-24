INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description, modified_date, modified_by, active, stage)
VALUES
(NOW(), 1, true, 'auto_disable_cash_mode', '8/20', 'This configuration is for disabling the cash mode by default for the time mentioned in 24 hour format with slash exp : 8am to 8pm 8/20', NULL, NULL, true, NULL)