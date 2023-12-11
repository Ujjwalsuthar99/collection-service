CREATE TABLE collection.repossession (
	repossession_id bigserial NOT NULL,
	created_date timestamptz NOT NULL,
	created_by int8 NOT NULL,
	deleted bool NULL DEFAULT false,
	loan_id int8 NOT NULL,
	status varchar(50) NOT NULL,
	remarks text NULL,
	lms_repo_id int8 NULL
);
CREATE INDEX repossession_loan_id_idx ON collection.repossession (loan_id,status,created_by,deleted);



CREATE sequence if not EXISTS collection.repossession_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;



ALTER SEQUENCE collection.repossession_id_seq OWNED BY collection.repossession.repossession_id;