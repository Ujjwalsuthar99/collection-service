INSERT INTO collection.collection_configurations (created_date,created_by,deleted,configuration_name,configuration_value,configuration_description,modified_date,modified_by,active,stage) VALUES
	 ('2023-10-28 17:04:11.345',1,false,'digital_transaction_payment_modes','["neft","upi"]','This configuration will enable and disable the payment modes in the dropdown list of digital payment',NULL,NULL,true,NULL),
	 ('2023-10-28 17:05:07.449',1,false,'is_dynamic_qr_code_enabled','false','By this configuration user can enable or disable the dynamic qr code feature by mentioning true',NULL,NULL,true,NULL),
	 ('2023-10-28 17:07:13.466',1,false,'qr_vendor_list','["kotak", "icici"]','This is the configuration where user can remove or add the vendors to generate the qr code.',NULL,NULL,true,NULL);
