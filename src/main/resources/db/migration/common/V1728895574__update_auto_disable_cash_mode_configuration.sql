UPDATE collection.collection_configurations SET configuration_value = '[{"enable": "false", "from": "8", "to": "20", "payment_mode": "coll_cash"}]' WHERE configuration_name = 'auto_disable_cash_mode';

UPDATE collection.collection_configurations SET configuration_name = 'auto_disable_payment_modes' WHERE configuration_name = 'auto_disable_cash_mode'
