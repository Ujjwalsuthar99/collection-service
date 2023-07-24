INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description, modified_date, modified_by, active, stage)
VALUES(NOW(), 1, false, 'geo_tagging_enabled_on_photos', 'false', 'This configuration enables the geo tagging on the photos with the current timestamp', NOW(), NULL, true, NULL);