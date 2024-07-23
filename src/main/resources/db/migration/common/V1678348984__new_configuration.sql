insert
	into
	collection.collection_configurations (created_date,
	created_by,
	deleted,
	configuration_name,
	configuration_value,
	configuration_description)
values
	 ('2023-02-24 11:36:24.757',
1,
false,
'currency_format_key',
'en-IN',
'rupee symbol to be used at frontend'),
	 ('2023-02-24 11:36:24.757',
1,
false,
'currency_format',
'INR',
'rupee symbol to be used at frontend'),
	 ('2023-01-27 13:01:20.000',
1,
false,
'backdated_receipts',
'true',
'Client can able to generate back dated receipts or not');

update
	collection.collection_configurations
set
	configuration_value = 'YYYY-MM-DD'
where
	configuration_name = 'date_format';

alter table collection.receipt_transfer alter column transferred_to_user_id drop not null;