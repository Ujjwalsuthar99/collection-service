ALTER TABLE collection.followups ADD column if not exists followup_status varchar(10) DEFAULT NULL;
ALTER TABLE collection.followups ADD column if not exists closing_remarks varchar(200) DEFAULT NULL;
ALTER TABLE collection.followups ADD column if not exists service_request_id int8 DEFAULT NULL;