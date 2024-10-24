INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description, modified_date, modified_by, active, stage)
VALUES
(NOW(), 1, false, 'hide_payment_bank_dropdown_for_upi', 'false', 'This configuration is only for upi cases where we need to hide the payment bank dropdown, values will be true/false', NULL, NULL, true, NULL)