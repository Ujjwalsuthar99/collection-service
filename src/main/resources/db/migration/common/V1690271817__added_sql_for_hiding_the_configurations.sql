update collection.collection_configurations set deleted=true where configuration_name in (
'attendance_enabled',
'auto_allocation_cleanup',
'client_app_logo',
'current_app_version_android',
'force_app_update_version_android',
'geo_fencing_enabled',
'show_payment_bank_dropdown',
'upload_client_logo_at_profile_page',
'payment_bank_dropdowns'
);