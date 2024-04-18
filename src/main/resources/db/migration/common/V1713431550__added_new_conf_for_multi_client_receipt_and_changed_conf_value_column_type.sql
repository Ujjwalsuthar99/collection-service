   INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description, modified_date, modified_by, active, stage)
VALUES(NOW(), 1, true, 'multi_receipt_client_credentials', 'false', 'This configuration will be used to post the receipts into provided client accounts from the current client', NOW(), NULL, true, NULL);



ALTER TABLE collection.collection_configurations ALTER COLUMN configuration_value TYPE text USING configuration_value::text;



ALTER TABLE collection.digital_payment_transactions ADD payment_proof_image text NULL;
ALTER TABLE collection.digital_payment_transactions ADD selfie_with_cx_image text NULL;