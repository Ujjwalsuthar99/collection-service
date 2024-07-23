INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description, modified_date, modified_by, active, stage)
VALUES('2023-01-27 13:01:20.000', 1, false, 'is_cheque_ocr_enabled', 'false', 'To enable cheque OCR service with the help of integration service then we can enable it by setting the value as true', '2023-04-26 12:21:28.543', NULL, true, NULL);

ALTER TABLE collection.collection_limit_userwise add column IF NOT EXISTS user_name VARCHAR DEFAULT NULL;

ALTER TABLE collection.collection_limit_userwise add column if not exists modified_date timestamptz;

ALTER TABLE collection.collection_limit_userwise add column if not exists modified_by varchar NULL;

ALTER TABLE collection.collection_limit_userwise add column if not exists active bool default true;

ALTER TABLE collection.collection_limit_userwise add column if not exists stage varchar;

ALTER TABLE collection.collection_limit_userwise add column if not exists created_by bigint NULL;

update collection.collection_configurations set configuration_description ='By mentioning the limit we can restrict the user to collect the money in cash within the limit.' where configuration_name='cash_collection_default_limit';

update collection.collection_configurations set configuration_description ='By mentioning the limit we can restrict the user to collect the money in cheque within the limit.' where configuration_name='cheque_collection_default_limit';

update collection.collection_configurations set configuration_description ='Client logo path of S3 bucket.' where configuration_name='client_app_logo';

update collection.collection_configurations set configuration_description ='To define the correct and current version of application needs to be used by the user.' where configuration_name='current_app_version_android';

update collection.collection_configurations set configuration_description ='By this configuration we can change the date format throughout the application.' where configuration_name='date_format';

update collection.collection_configurations set configuration_description ='By this configuration we can configure the maximum numbers to be captured while creating follow ups.' where configuration_name='followup_photos_count_max';

update collection.collection_configurations set configuration_description ='By this configuration we can configure the numbers numbers to be captured while creating follow ups.' where configuration_name='followup_photos_count_min';

update collection.collection_configurations set configuration_description ='By this configuration we can force the user to download the correct version of application.' where configuration_name='force_app_update_version_android';

update collection.collection_configurations set configuration_description ='By this configuration we can force the user to use his/her Id/Account in one device only.' where configuration_name='hard_binding_with_device';

update collection.collection_configurations set configuration_description ='By this configuration we can show or hide the contact numbers of loan participants.' where configuration_name='mask_all_customer_phone_numbers';

update collection.collection_configurations set configuration_description ='By this configuration we can set the limit to follow RBI guideline of collecting money more than 2 lakh in cash mode.' where configuration_name='per_day_cash_collection_customer_limit';

update collection.collection_configurations set configuration_description ='By this configuration we can enable the global search on the header of the application.' where configuration_name='search_in_all_loans';

update collection.collection_configurations set configuration_value='200000' where configuration_name='cash_collection_default_limit';

update collection.collection_configurations set configuration_value='200000' where configuration_name='per_day_cash_collection_customer_limit';

update collection.collection_configurations set configuration_value='false' where configuration_name='auto_allocation_read_only';

update collection.collection_configurations set configuration_value='false' where configuration_name='backdated_receipts';

update collection.collection_configurations set configuration_value='true' where configuration_name='use_business_date_as_receipt_trx_date';

update collection.collection_configurations set configuration_value='false' where configuration_name='add_contact_popup_at_create_receipt';

update collection.collection_configurations set configuration_value='true' where configuration_name='view_overdue_breakup_at_create_receipt';
