
DELETE FROM collection.collection_configurations WHERE configuration_name = 'side_navigation_panel';

INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description, modified_date, modified_by, active, stage)
VALUES('2023-01-27 13:01:20.000', 1, false, 'side_navigation_panel', '["receipt_transfer"]', 'If the configuration is blank then the common buttons will visible else in the array ', '2023-04-26 12:21:28.543', NULL, true, NULL);