INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description, modified_date, modified_by, active, stage)
VALUES
(NOW(), 1, true, 'static_payment_bank_for_upi', 'false', 'This config is for upi mode auto authored scenario, static payment bank code will be gone for LMS receipt. values will be false or any bank code present in LMS', NULL, NULL, true, NULL)
