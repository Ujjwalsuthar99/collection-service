INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description, modified_date, modified_by, active, stage)
VALUES('2023-12-12 13:01:20.000', 1, false, 'is_repossession_enabled', 'false', 'vehicle repossession true/false value', '2023-04-26 12:21:28.543', NULL, true, NULL),
('2023-12-12 13:01:20.000', 1, false, 'show_repossession_after_x_dpd', '60-90', 'if is_repossession_enabled then DPD of loan will be counted', '2023-04-26 12:21:28.543', NULL, true, NULL);
