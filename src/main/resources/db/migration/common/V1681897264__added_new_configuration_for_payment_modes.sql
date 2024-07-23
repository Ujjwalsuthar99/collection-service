INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description)
VALUES('2023-01-27 13:01:20.000', 1, false, 'payment_modes_to_use_at_create_receipt', 'coll_cash/coll_cheque/coll_upi', 'List all the payment modes here to enable and disable them at the time of receipt generation for all users.');

INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description)
VALUES('2023-01-27 13:01:20.000', 1, false, 'create_receipt_payment_proof_image_mandate', 'false', 'Set true or false to mandate the image upload field while creating receipt.');

INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description)
VALUES('2023-01-27 13:01:20.000', 1, false, 'create_receipt_selfie_with_customer_image_mandate', 'false', 'Set true or false to mandate the image upload field while creating receipt.');

INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description)
VALUES('2023-01-27 13:01:20.000', 1, false, 'receipt_transfer_sender_image_mandate', 'false', 'Set true or false to mandate the image upload field while creating receipt.');

INSERT INTO collection.collection_configurations
(created_date, created_by, deleted, configuration_name, configuration_value, configuration_description)
VALUES('2023-01-27 13:01:20.000', 1, false, 'receipt_transfer_receiver_approval_image_mandate', 'false', 'Set true or false to mandate the image upload field while creating receipt.');
