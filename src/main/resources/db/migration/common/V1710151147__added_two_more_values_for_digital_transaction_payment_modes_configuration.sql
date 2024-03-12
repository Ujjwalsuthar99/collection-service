UPDATE collection.collection_configurations
SET configuration_value = '["neft","upi","rtgs"]'
WHERE configuration_name = 'digital_transaction_payment_modes';
