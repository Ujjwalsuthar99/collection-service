ALTER TABLE master.support_contact_details ADD modified_by int8 NULL;
ALTER TABLE master.support_contact_details ADD stage varchar NULL;
ALTER TABLE master.support_contact_details ADD active bool NULL;
ALTER TABLE master.support_contact_details ADD modified_date timestamptz NULL;