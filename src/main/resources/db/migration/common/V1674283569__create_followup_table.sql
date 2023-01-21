CREATE TABLE IF NOT EXISTS collection.followup (
	followup_id bigserial NOT NULL,
	loan_id int8 NOT NULL,
	deleted bool NOT NULL DEFAULT false,
	created_date timestamptz NOT NULL,
	created_by int8 NOT NULL,
	followup_reason varchar(100) NOT NULL,
	followup_datetime timestamptz NOT NULL,
	other_followup_reason varchar(200) NULL,
	remarks text NULL,
	CONSTRAINT followup_pk PRIMARY KEY (followup_id)
);
CREATE INDEX followup_loan_id_idx ON collection.followup USING btree (loan_id, deleted);