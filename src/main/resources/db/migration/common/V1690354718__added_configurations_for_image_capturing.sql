INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description, modified_date, modified_by, active, stage)
VALUES('2023-07-21 13:01:20.000', 1, false, 'image_capturing_via', 'both', 'Camera and both configuration if camera then only camera will open other wise both option will be visible', '2023-07-21 12:21:28.543', NULL, true, NULL);

update collection.collection_configurations set configuration_value = 'true' where configuration_name = 'geo_tagging_enabled_on_photos';