--
-- PostgreSQL database dump
--

-- Dumped from database version 15.1
-- Dumped by pg_dump version 15.1

-- Started on 2023-02-06 17:48:56

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 7 (class 2615 OID 61210)
-- Name: collection; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA IF NOT EXISTS collection;


ALTER SCHEMA collection OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 219 (class 1259 OID 61212)
-- Name: additional_contact_details; Type: TABLE; Schema: collection; Owner: postgres
--

CREATE TABLE collection.additional_contact_details (
    additional_contact_detail_id bigint NOT NULL,
    created_date timestamp with time zone NOT NULL,
    created_by bigint NOT NULL,
    deleted boolean DEFAULT false,
    loan_id bigint NOT NULL,
    contact_name character varying(100) NOT NULL,
    mobile_no bigint NOT NULL,
    alt_mobile_no bigint,
    email character varying(50) NOT NULL,
    relation_with_applicant character varying(50) NOT NULL
);


ALTER TABLE collection.additional_contact_details OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 61216)
-- Name: additional_contact_details_additional_contact_detail_id_seq; Type: SEQUENCE; Schema: collection; Owner: postgres
--

CREATE SEQUENCE collection.additional_contact_details_additional_contact_detail_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE collection.additional_contact_details_additional_contact_detail_id_seq OWNER TO postgres;

--
-- TOC entry 3454 (class 0 OID 0)
-- Dependencies: 220
-- Name: additional_contact_details_additional_contact_detail_id_seq; Type: SEQUENCE OWNED BY; Schema: collection; Owner: postgres
--

ALTER SEQUENCE collection.additional_contact_details_additional_contact_detail_id_seq OWNED BY collection.additional_contact_details.additional_contact_detail_id;


--
-- TOC entry 221 (class 1259 OID 61217)
-- Name: collection_activity_logs; Type: TABLE; Schema: collection; Owner: postgres
--

CREATE TABLE collection.collection_activity_logs (
    collection_activity_logs_id bigint NOT NULL,
    activity_date timestamp with time zone NOT NULL,
    activity_by bigint NOT NULL,
    deleted boolean DEFAULT false,
    activity_name character varying(100) NOT NULL,
    distance_from_user_branch numeric NOT NULL,
    address json NOT NULL,
    remarks text NOT NULL,
    images json NOT NULL,
    loan_id bigint,
    geo_location_data json
);


ALTER TABLE collection.collection_activity_logs OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 61223)
-- Name: collection_activity_logs_collection_activity_logs_id_seq; Type: SEQUENCE; Schema: collection; Owner: postgres
--

CREATE SEQUENCE collection.collection_activity_logs_collection_activity_logs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE collection.collection_activity_logs_collection_activity_logs_id_seq OWNER TO postgres;

--
-- TOC entry 3455 (class 0 OID 0)
-- Dependencies: 222
-- Name: collection_activity_logs_collection_activity_logs_id_seq; Type: SEQUENCE OWNED BY; Schema: collection; Owner: postgres
--

ALTER SEQUENCE collection.collection_activity_logs_collection_activity_logs_id_seq OWNED BY collection.collection_activity_logs.collection_activity_logs_id;


--
-- TOC entry 223 (class 1259 OID 61224)
-- Name: collection_configurations; Type: TABLE; Schema: collection; Owner: postgres
--

CREATE TABLE collection.collection_configurations (
    configuration_id bigint NOT NULL,
    created_date timestamp with time zone NOT NULL,
    created_by bigint NOT NULL,
    deleted boolean DEFAULT false,
    configuration_name character varying(50) NOT NULL,
    configuration_value character varying(100) NOT NULL,
    configuration_description text
);


ALTER TABLE collection.collection_configurations OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 61230)
-- Name: collection_configurations_configuration_id_seq; Type: SEQUENCE; Schema: collection; Owner: postgres
--

CREATE SEQUENCE collection.collection_configurations_configuration_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE collection.collection_configurations_configuration_id_seq OWNER TO postgres;

--
-- TOC entry 3456 (class 0 OID 0)
-- Dependencies: 224
-- Name: collection_configurations_configuration_id_seq; Type: SEQUENCE OWNED BY; Schema: collection; Owner: postgres
--

ALTER SEQUENCE collection.collection_configurations_configuration_id_seq OWNED BY collection.collection_configurations.configuration_id;


--
-- TOC entry 225 (class 1259 OID 61231)
-- Name: collection_limit_userwise; Type: TABLE; Schema: collection; Owner: postgres
--

CREATE TABLE collection.collection_limit_userwise (
    collection_limit_definitions_id bigint NOT NULL,
    created_date timestamp with time zone NOT NULL,
    deleted boolean DEFAULT false,
    collection_limit_strategies_key character varying NOT NULL,
    user_id bigint NOT NULL,
    total_limit_value numeric DEFAULT 0 NOT NULL,
    utilized_limit_value numeric DEFAULT 0 NOT NULL
);


ALTER TABLE collection.collection_limit_userwise OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 61239)
-- Name: collection_limit_definitions_collection_limit_definitions_i_seq; Type: SEQUENCE; Schema: collection; Owner: postgres
--

CREATE SEQUENCE collection.collection_limit_definitions_collection_limit_definitions_i_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE collection.collection_limit_definitions_collection_limit_definitions_i_seq OWNER TO postgres;

--
-- TOC entry 3457 (class 0 OID 0)
-- Dependencies: 226
-- Name: collection_limit_definitions_collection_limit_definitions_i_seq; Type: SEQUENCE OWNED BY; Schema: collection; Owner: postgres
--

ALTER SEQUENCE collection.collection_limit_definitions_collection_limit_definitions_i_seq OWNED BY collection.collection_limit_userwise.collection_limit_definitions_id;


--
-- TOC entry 227 (class 1259 OID 61240)
-- Name: collection_receipts; Type: TABLE; Schema: collection; Owner: postgres
--

CREATE TABLE collection.collection_receipts (
    receipt_id bigint NOT NULL,
    created_by bigint NOT NULL,
    receipt_holder_user_id bigint NOT NULL,
    last_receipt_transfer_id bigint,
    collection_activity_logs_id bigint
);


ALTER TABLE collection.collection_receipts OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 61243)
-- Name: followups; Type: TABLE; Schema: collection; Owner: postgres
--

CREATE TABLE collection.followups (
    followups_id bigint NOT NULL,
    loan_id bigint NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    created_date timestamp with time zone NOT NULL,
    created_by bigint NOT NULL,
    followup_reason character varying(100) NOT NULL,
    next_followup_datetime timestamp with time zone NOT NULL,
    other_followup_reason character varying(200),
    remarks text,
    collection_activity_logs_id bigint
);


ALTER TABLE collection.followups OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 61249)
-- Name: followups_followups_id_seq; Type: SEQUENCE; Schema: collection; Owner: postgres
--

CREATE SEQUENCE collection.followups_followups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE collection.followups_followups_id_seq OWNER TO postgres;

--
-- TOC entry 3458 (class 0 OID 0)
-- Dependencies: 229
-- Name: followups_followups_id_seq; Type: SEQUENCE OWNED BY; Schema: collection; Owner: postgres
--

ALTER SEQUENCE collection.followups_followups_id_seq OWNED BY collection.followups.followups_id;


--
-- TOC entry 230 (class 1259 OID 61250)
-- Name: loan_allocation; Type: TABLE; Schema: collection; Owner: postgres
--

CREATE TABLE collection.loan_allocation (
    loan_allocation_id bigint NOT NULL,
    created_date timestamp with time zone NOT NULL,
    created_by bigint NOT NULL,
    deleted boolean DEFAULT false,
    loan_id bigint NOT NULL,
    allocated_to_user_id bigint NOT NULL
);


ALTER TABLE collection.loan_allocation OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 61254)
-- Name: loan_allocation_loan_allocation_id_seq; Type: SEQUENCE; Schema: collection; Owner: postgres
--

CREATE SEQUENCE collection.loan_allocation_loan_allocation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE collection.loan_allocation_loan_allocation_id_seq OWNER TO postgres;

--
-- TOC entry 3459 (class 0 OID 0)
-- Dependencies: 231
-- Name: loan_allocation_loan_allocation_id_seq; Type: SEQUENCE OWNED BY; Schema: collection; Owner: postgres
--

ALTER SEQUENCE collection.loan_allocation_loan_allocation_id_seq OWNED BY collection.loan_allocation.loan_allocation_id;


--
-- TOC entry 232 (class 1259 OID 61255)
-- Name: receipt_transfer; Type: TABLE; Schema: collection; Owner: postgres
--

CREATE TABLE collection.receipt_transfer (
    receipt_transfer_id bigint NOT NULL,
    created_date timestamp with time zone NOT NULL,
    transferred_by bigint NOT NULL,
    deleted boolean DEFAULT false,
    transfer_type character varying(50) NOT NULL,
    transfer_mode character varying(50) NOT NULL,
    transferred_to_user_id bigint NOT NULL,
    amount numeric NOT NULL,
    receipt_image json NOT NULL,
    status character varying(100) NOT NULL,
    remarks text,
    transfer_bank_code character varying(50),
    action_datetime timestamp with time zone,
    action_reason text,
    action_remarks text,
    action_by bigint,
    collection_activity_logs_id bigint NOT NULL
);


ALTER TABLE collection.receipt_transfer OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 61330)
-- Name: receipt_transfer_history; Type: TABLE; Schema: collection; Owner: postgres
--

CREATE TABLE collection.receipt_transfer_history (
    receipt_transfer_history_id bigint NOT NULL,
    receipt_transfer_id bigint NOT NULL,
    collection_receipts_id bigint NOT NULL
);


ALTER TABLE collection.receipt_transfer_history OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 61329)
-- Name: receipt_transfer_history_receipt_transfer_history_id_seq; Type: SEQUENCE; Schema: collection; Owner: postgres
--

CREATE SEQUENCE collection.receipt_transfer_history_receipt_transfer_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE collection.receipt_transfer_history_receipt_transfer_history_id_seq OWNER TO postgres;

--
-- TOC entry 3460 (class 0 OID 0)
-- Dependencies: 238
-- Name: receipt_transfer_history_receipt_transfer_history_id_seq; Type: SEQUENCE OWNED BY; Schema: collection; Owner: postgres
--

ALTER SEQUENCE collection.receipt_transfer_history_receipt_transfer_history_id_seq OWNED BY collection.receipt_transfer_history.receipt_transfer_history_id;


--
-- TOC entry 233 (class 1259 OID 61264)
-- Name: receipt_transfer_receipt_transfer_id_seq; Type: SEQUENCE; Schema: collection; Owner: postgres
--

CREATE SEQUENCE collection.receipt_transfer_receipt_transfer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE collection.receipt_transfer_receipt_transfer_id_seq OWNER TO postgres;

--
-- TOC entry 3461 (class 0 OID 0)
-- Dependencies: 233
-- Name: receipt_transfer_receipt_transfer_id_seq; Type: SEQUENCE OWNED BY; Schema: collection; Owner: postgres
--

ALTER SEQUENCE collection.receipt_transfer_receipt_transfer_id_seq OWNED BY collection.receipt_transfer.receipt_transfer_id;


--
-- TOC entry 234 (class 1259 OID 61265)
-- Name: registered_device_info; Type: TABLE; Schema: collection; Owner: postgres
--

CREATE TABLE collection.registered_device_info (
    registered_device_info_id bigint NOT NULL,
    created_date timestamp with time zone NOT NULL,
    created_by bigint NOT NULL,
    deleted boolean DEFAULT false,
    user_id bigint NOT NULL,
    last_app_usages timestamp with time zone NOT NULL,
    current_app_version character varying(50) NOT NULL,
    platform character varying(50) NOT NULL,
    platform_version character varying(50) NOT NULL,
    device_unique_id character varying(50) NOT NULL,
    device_manufacturer_name character varying(50) NOT NULL,
    device_model character varying(50) NOT NULL,
    status character varying(50) NOT NULL
);


ALTER TABLE collection.registered_device_info OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 61269)
-- Name: registered_device_info_registered_device_info_id_seq; Type: SEQUENCE; Schema: collection; Owner: postgres
--

CREATE SEQUENCE collection.registered_device_info_registered_device_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE collection.registered_device_info_registered_device_info_id_seq OWNER TO postgres;

--
-- TOC entry 3462 (class 0 OID 0)
-- Dependencies: 235
-- Name: registered_device_info_registered_device_info_id_seq; Type: SEQUENCE OWNED BY; Schema: collection; Owner: postgres
--

ALTER SEQUENCE collection.registered_device_info_registered_device_info_id_seq OWNED BY collection.registered_device_info.registered_device_info_id;


--
-- TOC entry 3230 (class 2604 OID 61275)
-- Name: additional_contact_details additional_contact_detail_id; Type: DEFAULT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.additional_contact_details ALTER COLUMN additional_contact_detail_id SET DEFAULT nextval('collection.additional_contact_details_additional_contact_detail_id_seq'::regclass);


--
-- TOC entry 3232 (class 2604 OID 61276)
-- Name: collection_activity_logs collection_activity_logs_id; Type: DEFAULT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.collection_activity_logs ALTER COLUMN collection_activity_logs_id SET DEFAULT nextval('collection.collection_activity_logs_collection_activity_logs_id_seq'::regclass);


--
-- TOC entry 3234 (class 2604 OID 61277)
-- Name: collection_configurations configuration_id; Type: DEFAULT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.collection_configurations ALTER COLUMN configuration_id SET DEFAULT nextval('collection.collection_configurations_configuration_id_seq'::regclass);


--
-- TOC entry 3236 (class 2604 OID 61278)
-- Name: collection_limit_userwise collection_limit_definitions_id; Type: DEFAULT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.collection_limit_userwise ALTER COLUMN collection_limit_definitions_id SET DEFAULT nextval('collection.collection_limit_definitions_collection_limit_definitions_i_seq'::regclass);


--
-- TOC entry 3240 (class 2604 OID 61279)
-- Name: followups followups_id; Type: DEFAULT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.followups ALTER COLUMN followups_id SET DEFAULT nextval('collection.followups_followups_id_seq'::regclass);


--
-- TOC entry 3242 (class 2604 OID 61280)
-- Name: loan_allocation loan_allocation_id; Type: DEFAULT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.loan_allocation ALTER COLUMN loan_allocation_id SET DEFAULT nextval('collection.loan_allocation_loan_allocation_id_seq'::regclass);


--
-- TOC entry 3244 (class 2604 OID 61281)
-- Name: receipt_transfer receipt_transfer_id; Type: DEFAULT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.receipt_transfer ALTER COLUMN receipt_transfer_id SET DEFAULT nextval('collection.receipt_transfer_receipt_transfer_id_seq'::regclass);


--
-- TOC entry 3248 (class 2604 OID 61333)
-- Name: receipt_transfer_history receipt_transfer_history_id; Type: DEFAULT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.receipt_transfer_history ALTER COLUMN receipt_transfer_history_id SET DEFAULT nextval('collection.receipt_transfer_history_receipt_transfer_history_id_seq'::regclass);


--
-- TOC entry 3246 (class 2604 OID 61282)
-- Name: registered_device_info registered_device_info_id; Type: DEFAULT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.registered_device_info ALTER COLUMN registered_device_info_id SET DEFAULT nextval('collection.registered_device_info_registered_device_info_id_seq'::regclass);


--
-- TOC entry 3430 (class 0 OID 61212)
-- Dependencies: 219
-- Data for Name: additional_contact_details; Type: TABLE DATA; Schema: collection; Owner: postgres
--

INSERT INTO collection.additional_contact_details VALUES (1, '2023-01-31 12:00:00+05:30', 1234, false, 1234, 'ujjwal', 9649916989, 8006875754, 'ujjwal@synoriq.in', 'applicant');
INSERT INTO collection.additional_contact_details VALUES (2, '2023-01-31 12:00:00+05:30', 1234, false, 1234, 'ujjwal', 9649916989, 8006875754, 'ujjwal@synoriq.in', 'applicant');
INSERT INTO collection.additional_contact_details VALUES (3, '2023-02-04 15:36:17.569+05:30', 123, NULL, 210010304, 'contactName', 8000000000, 9000000000, 'email', 'relationWithApplicant');
INSERT INTO collection.additional_contact_details VALUES (4, '2023-02-04 15:36:42.861+05:30', 123, NULL, 210010304, 'contactName', 8000000000, 9000000000, 'email', 'relationWithApplicant');
INSERT INTO collection.additional_contact_details VALUES (5, '2023-02-04 15:37:09.884+05:30', 123, NULL, 210010304, 'contactName', 8000000000, 9000000000, 'email', 'relationWithApplicant');
INSERT INTO collection.additional_contact_details VALUES (6, '2023-02-04 15:37:42.506+05:30', 8778, NULL, 210010304, 'contactName', 8000000000, 9000000000, 'email', 'relationWithApplicant');


--
-- TOC entry 3432 (class 0 OID 61217)
-- Dependencies: 221
-- Data for Name: collection_activity_logs; Type: TABLE DATA; Schema: collection; Owner: postgres
--



--
-- TOC entry 3434 (class 0 OID 61224)
-- Dependencies: 223
-- Data for Name: collection_configurations; Type: TABLE DATA; Schema: collection; Owner: postgres
--

INSERT INTO collection.collection_configurations VALUES (3, '2023-01-27 13:01:20+05:30', 1, false, 'client_app_logo', 'https://login.synofin.tech/static/media/synofinlogo.1bea58d165dfe1791f571baac2d80a28.svg', NULL);
INSERT INTO collection.collection_configurations VALUES (4, '2023-01-27 13:01:20+05:30', 1, false, 'date_format', 'dd-mm-yyyy', NULL);
INSERT INTO collection.collection_configurations VALUES (5, '2023-01-27 13:01:20+05:30', 1, false, 'force_app_update_version_android', '1.0.0', NULL);
INSERT INTO collection.collection_configurations VALUES (6, '2023-01-27 13:01:20+05:30', 1, false, 'current_app_version_android', '1.0.0', NULL);
INSERT INTO collection.collection_configurations VALUES (7, '2023-01-27 13:01:20+05:30', 1, false, 'attendance_enabled', 'false', NULL);
INSERT INTO collection.collection_configurations VALUES (8, '2023-01-27 13:01:20+05:30', 1, false, 'geo_fencing_enabled', 'false', NULL);
INSERT INTO collection.collection_configurations VALUES (9, '2023-01-27 13:01:20+05:30', 1, false, 'use_business_date_as_receipt_trx_date', 'false', NULL);
INSERT INTO collection.collection_configurations VALUES (10, '2023-01-27 13:01:20+05:30', 1, false, 'search_in_all_loans', 'false', NULL);
INSERT INTO collection.collection_configurations VALUES (11, '2023-01-27 13:01:20+05:30', 1, false, 'followup_photos_count_max', '2', NULL);
INSERT INTO collection.collection_configurations VALUES (12, '2023-01-27 13:01:20+05:30', 1, false, 'followup_photos_count_min', '1', NULL);
INSERT INTO collection.collection_configurations VALUES (13, '2023-01-27 13:01:20+05:30', 1, false, 'auto_allocation_cleanup', 'false', NULL);
INSERT INTO collection.collection_configurations VALUES (14, '2023-01-27 13:01:20+05:30', 1, false, 'hard_binding_with_device', 'false', 'Do not allow user to login in different device.');
INSERT INTO collection.collection_configurations VALUES (16, '2023-01-27 13:01:20+05:30', 1, false, 'per_day_cash_collection_customer_limit', '20000', NULL);
INSERT INTO collection.collection_configurations VALUES (17, '2023-01-27 13:01:20+05:30', 1, false, 'cheque_collection_default_limit', '300000', NULL);
INSERT INTO collection.collection_configurations VALUES (15, '2023-01-27 13:01:20+05:30', 1, false, 'cash_collection_default_limit', '20000', '');


--
-- TOC entry 3436 (class 0 OID 61231)
-- Dependencies: 225
-- Data for Name: collection_limit_userwise; Type: TABLE DATA; Schema: collection; Owner: postgres
--



--
-- TOC entry 3438 (class 0 OID 61240)
-- Dependencies: 227
-- Data for Name: collection_receipts; Type: TABLE DATA; Schema: collection; Owner: postgres
--



--
-- TOC entry 3439 (class 0 OID 61243)
-- Dependencies: 228
-- Data for Name: followups; Type: TABLE DATA; Schema: collection; Owner: postgres
--



--
-- TOC entry 3441 (class 0 OID 61250)
-- Dependencies: 230
-- Data for Name: loan_allocation; Type: TABLE DATA; Schema: collection; Owner: postgres
--

INSERT INTO collection.loan_allocation VALUES (26, '2023-02-03 12:56:15.06+05:30', 123, false, 500001, 1104);
INSERT INTO collection.loan_allocation VALUES (27, '2023-02-03 12:56:15.275+05:30', 123, false, 500002, 1104);
INSERT INTO collection.loan_allocation VALUES (40, '2023-02-04 11:13:29.569+05:30', 123, false, 500003, 1104);
INSERT INTO collection.loan_allocation VALUES (41, '2023-02-04 11:13:29.641+05:30', 123, false, 500004, 1104);
INSERT INTO collection.loan_allocation VALUES (42, '2023-02-04 11:13:29.646+05:30', 123, false, 500005, 1104);
INSERT INTO collection.loan_allocation VALUES (43, '2023-02-04 11:13:29.652+05:30', 123, false, 500006, 1104);


--
-- TOC entry 3443 (class 0 OID 61255)
-- Dependencies: 232
-- Data for Name: receipt_transfer; Type: TABLE DATA; Schema: collection; Owner: postgres
--

INSERT INTO collection.receipt_transfer VALUES (19, '2023-02-04 13:35:04.107+05:30', 1104, false, 'branch', 'cash', 1809, 50000, '{"url_1":"https://www.google.com/url?sa=i&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FSupercomputer&psig=AOvVaw0U0YyvzeemeLH3ddNt0g1C&ust=1675339770857000&source=images&cd=vfe&ved=0CBAQjRxqFwoTCJCV6I-l9PwCFQAAAAAdAAAAABAM","url_2":"https://www.google.com/url?sa=i&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FSupercomputer&psig=AOvVaw0U0YyvzeemeLH3ddNt0g1C&ust=1675339770857000&source=images&cd=vfe&ved=0CBAQjRxqFwoTCJCV6I-l9PwCFQAAAAAdAAAAABAM"}', 'Approved', NULL, 'IOBA', '2023-02-01 05:30:00+05:30', 'cash received', 'I have received the cash from Samyak', 1809, 4);


--
-- TOC entry 3448 (class 0 OID 61330)
-- Dependencies: 239
-- Data for Name: receipt_transfer_history; Type: TABLE DATA; Schema: collection; Owner: postgres
--

INSERT INTO collection.receipt_transfer_history VALUES (41, 19, 1);
INSERT INTO collection.receipt_transfer_history VALUES (42, 19, 2);
INSERT INTO collection.receipt_transfer_history VALUES (43, 19, 3);
INSERT INTO collection.receipt_transfer_history VALUES (44, 19, 4);


--
-- TOC entry 3445 (class 0 OID 61265)
-- Dependencies: 234
-- Data for Name: registered_device_info; Type: TABLE DATA; Schema: collection; Owner: postgres
--

INSERT INTO collection.registered_device_info VALUES (18, '2023-01-31 11:44:59.834+05:30', 1, false, 123, '2023-01-31 11:44:59.834+05:30', '1.2', 'android', '7', '34567123asdaeqweq3eq2x_7qye', 'IQOO', '9', 'Inactive');
INSERT INTO collection.registered_device_info VALUES (20, '2023-01-31 11:56:35.779+05:30', 1, NULL, 123, '2023-01-31 11:56:35.779+05:30', '1.2', 'android', '7', 'asdasd7123asdaeqweq3eq2x_7qye', 'IQOO', '9', 'Inactive');
INSERT INTO collection.registered_device_info VALUES (14, '2023-01-31 10:48:34.739+05:30', 1, false, 123, '2023-01-31 10:48:34.739+05:30', '1.2', 'android', '7', '123asdaeqweq3eq2x_72qye', 'IQOO', '9', 'Inactive');
INSERT INTO collection.registered_device_info VALUES (24, '2023-01-31 12:33:07.242+05:30', 1, NULL, 12376453, '2023-01-31 12:33:07.242+05:30', '1.2', 'android', '7', '0000rtyuiopoiuyt--0000', 'IQOO', '9', 'Inactive');
INSERT INTO collection.registered_device_info VALUES (2, '2023-01-30 00:00:00+05:30', 1, false, 123, '2023-01-30 00:00:00+05:30', '1', 'android', '13', 'qwzzzxzzx31231x', 'IQOO', '9', 'Inactive');
INSERT INTO collection.registered_device_info VALUES (22, '2023-01-31 12:00:19.741+05:30', 1, NULL, 123, '2023-01-31 12:00:19.741+05:30', '1.2', 'android', '7', '0000rtyuiopoiuyt--0000', 'IQOO', '9', 'Inactive');


--
-- TOC entry 3463 (class 0 OID 0)
-- Dependencies: 220
-- Name: additional_contact_details_additional_contact_detail_id_seq; Type: SEQUENCE SET; Schema: collection; Owner: postgres
--

SELECT pg_catalog.setval('collection.additional_contact_details_additional_contact_detail_id_seq', 6, true);


--
-- TOC entry 3464 (class 0 OID 0)
-- Dependencies: 222
-- Name: collection_activity_logs_collection_activity_logs_id_seq; Type: SEQUENCE SET; Schema: collection; Owner: postgres
--

SELECT pg_catalog.setval('collection.collection_activity_logs_collection_activity_logs_id_seq', 4, true);


--
-- TOC entry 3465 (class 0 OID 0)
-- Dependencies: 224
-- Name: collection_configurations_configuration_id_seq; Type: SEQUENCE SET; Schema: collection; Owner: postgres
--

SELECT pg_catalog.setval('collection.collection_configurations_configuration_id_seq', 17, true);


--
-- TOC entry 3466 (class 0 OID 0)
-- Dependencies: 226
-- Name: collection_limit_definitions_collection_limit_definitions_i_seq; Type: SEQUENCE SET; Schema: collection; Owner: postgres
--

SELECT pg_catalog.setval('collection.collection_limit_definitions_collection_limit_definitions_i_seq', 1, false);


--
-- TOC entry 3467 (class 0 OID 0)
-- Dependencies: 229
-- Name: followups_followups_id_seq; Type: SEQUENCE SET; Schema: collection; Owner: postgres
--

SELECT pg_catalog.setval('collection.followups_followups_id_seq', 1, false);


--
-- TOC entry 3468 (class 0 OID 0)
-- Dependencies: 231
-- Name: loan_allocation_loan_allocation_id_seq; Type: SEQUENCE SET; Schema: collection; Owner: postgres
--

SELECT pg_catalog.setval('collection.loan_allocation_loan_allocation_id_seq', 43, true);


--
-- TOC entry 3469 (class 0 OID 0)
-- Dependencies: 238
-- Name: receipt_transfer_history_receipt_transfer_history_id_seq; Type: SEQUENCE SET; Schema: collection; Owner: postgres
--

SELECT pg_catalog.setval('collection.receipt_transfer_history_receipt_transfer_history_id_seq', 44, true);


--
-- TOC entry 3470 (class 0 OID 0)
-- Dependencies: 233
-- Name: receipt_transfer_receipt_transfer_id_seq; Type: SEQUENCE SET; Schema: collection; Owner: postgres
--

SELECT pg_catalog.setval('collection.receipt_transfer_receipt_transfer_id_seq', 19, true);


--
-- TOC entry 3471 (class 0 OID 0)
-- Dependencies: 235
-- Name: registered_device_info_registered_device_info_id_seq; Type: SEQUENCE SET; Schema: collection; Owner: postgres
--

SELECT pg_catalog.setval('collection.registered_device_info_registered_device_info_id_seq', 24, true);


--
-- TOC entry 3251 (class 2606 OID 61285)
-- Name: additional_contact_details additional_contact_details_pk; Type: CONSTRAINT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.additional_contact_details
    ADD CONSTRAINT additional_contact_details_pk PRIMARY KEY (additional_contact_detail_id);


--
-- TOC entry 3257 (class 2606 OID 61287)
-- Name: collection_configurations collection_configurations_pk; Type: CONSTRAINT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.collection_configurations
    ADD CONSTRAINT collection_configurations_pk PRIMARY KEY (configuration_id);


--
-- TOC entry 3259 (class 2606 OID 61289)
-- Name: collection_configurations collection_configurations_un; Type: CONSTRAINT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.collection_configurations
    ADD CONSTRAINT collection_configurations_un UNIQUE (configuration_name);


--
-- TOC entry 3261 (class 2606 OID 61291)
-- Name: collection_limit_userwise collection_limit_definition_pk; Type: CONSTRAINT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.collection_limit_userwise
    ADD CONSTRAINT collection_limit_definition_pk PRIMARY KEY (collection_limit_definitions_id);


--
-- TOC entry 3255 (class 2606 OID 61293)
-- Name: collection_activity_logs events_pkey; Type: CONSTRAINT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.collection_activity_logs
    ADD CONSTRAINT events_pkey PRIMARY KEY (collection_activity_logs_id);


--
-- TOC entry 3270 (class 2606 OID 61295)
-- Name: followups followup_pk; Type: CONSTRAINT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.followups
    ADD CONSTRAINT followup_pk PRIMARY KEY (followups_id);


--
-- TOC entry 3272 (class 2606 OID 61297)
-- Name: loan_allocation loan_allocation_un; Type: CONSTRAINT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.loan_allocation
    ADD CONSTRAINT loan_allocation_un UNIQUE (deleted, loan_id, allocated_to_user_id);


--
-- TOC entry 3275 (class 2606 OID 61299)
-- Name: loan_allocation loan_user_mapping_pkey; Type: CONSTRAINT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.loan_allocation
    ADD CONSTRAINT loan_user_mapping_pkey PRIMARY KEY (loan_allocation_id);


--
-- TOC entry 3286 (class 2606 OID 61335)
-- Name: receipt_transfer_history receipt_transfer_history_pkey; Type: CONSTRAINT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.receipt_transfer_history
    ADD CONSTRAINT receipt_transfer_history_pkey PRIMARY KEY (receipt_transfer_history_id);


--
-- TOC entry 3278 (class 2606 OID 61303)
-- Name: receipt_transfer receipt_transfer_pkey; Type: CONSTRAINT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.receipt_transfer
    ADD CONSTRAINT receipt_transfer_pkey PRIMARY KEY (receipt_transfer_id);


--
-- TOC entry 3266 (class 2606 OID 61305)
-- Name: collection_receipts receipt_transfer_receipt_mapping_pkey; Type: CONSTRAINT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.collection_receipts
    ADD CONSTRAINT receipt_transfer_receipt_mapping_pkey PRIMARY KEY (receipt_id);


--
-- TOC entry 3281 (class 2606 OID 61307)
-- Name: registered_device_info registered_device_info_pkey; Type: CONSTRAINT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.registered_device_info
    ADD CONSTRAINT registered_device_info_pkey PRIMARY KEY (registered_device_info_id);


--
-- TOC entry 3283 (class 2606 OID 61328)
-- Name: registered_device_info registered_device_info_un; Type: CONSTRAINT; Schema: collection; Owner: postgres
--

ALTER TABLE ONLY collection.registered_device_info
    ADD CONSTRAINT registered_device_info_un UNIQUE (user_id, device_unique_id);


--
-- TOC entry 3249 (class 1259 OID 61310)
-- Name: additional_contact_details_deleted_idx; Type: INDEX; Schema: collection; Owner: postgres
--

CREATE INDEX additional_contact_details_deleted_idx ON collection.additional_contact_details USING btree (deleted, loan_id);


--
-- TOC entry 3252 (class 1259 OID 61311)
-- Name: collection_activity_logs_activity_by_idx; Type: INDEX; Schema: collection; Owner: postgres
--

CREATE INDEX collection_activity_logs_activity_by_idx ON collection.collection_activity_logs USING btree (activity_by);


--
-- TOC entry 3253 (class 1259 OID 61312)
-- Name: collection_activity_logs_loan_id_idx; Type: INDEX; Schema: collection; Owner: postgres
--

CREATE INDEX collection_activity_logs_loan_id_idx ON collection.collection_activity_logs USING btree (loan_id);


--
-- TOC entry 3262 (class 1259 OID 61313)
-- Name: collection_limit_definitions_deleted_idx; Type: INDEX; Schema: collection; Owner: postgres
--

CREATE INDEX collection_limit_definitions_deleted_idx ON collection.collection_limit_userwise USING btree (deleted, user_id);


--
-- TOC entry 3263 (class 1259 OID 61314)
-- Name: collection_receipts_assigned_user_id_idx; Type: INDEX; Schema: collection; Owner: postgres
--

CREATE INDEX collection_receipts_assigned_user_id_idx ON collection.collection_receipts USING btree (receipt_holder_user_id);


--
-- TOC entry 3264 (class 1259 OID 61315)
-- Name: collection_receipts_receipt_id_idx; Type: INDEX; Schema: collection; Owner: postgres
--

CREATE INDEX collection_receipts_receipt_id_idx ON collection.collection_receipts USING btree (receipt_id);


--
-- TOC entry 3267 (class 1259 OID 61316)
-- Name: followup_collection_activity_logs_id_idx; Type: INDEX; Schema: collection; Owner: postgres
--

CREATE INDEX followup_collection_activity_logs_id_idx ON collection.followups USING btree (collection_activity_logs_id, deleted);


--
-- TOC entry 3268 (class 1259 OID 61317)
-- Name: followup_loan_id_idx; Type: INDEX; Schema: collection; Owner: postgres
--

CREATE INDEX followup_loan_id_idx ON collection.followups USING btree (loan_id, deleted);


--
-- TOC entry 3273 (class 1259 OID 61318)
-- Name: loan_user_mapping_loan_id_idx; Type: INDEX; Schema: collection; Owner: postgres
--

CREATE INDEX loan_user_mapping_loan_id_idx ON collection.loan_allocation USING btree (loan_id);


--
-- TOC entry 3276 (class 1259 OID 61319)
-- Name: loan_user_mapping_user_id_idx; Type: INDEX; Schema: collection; Owner: postgres
--

CREATE INDEX loan_user_mapping_user_id_idx ON collection.loan_allocation USING btree (allocated_to_user_id);


--
-- TOC entry 3287 (class 1259 OID 61336)
-- Name: receipt_transfer_history_receipt_id_idx; Type: INDEX; Schema: collection; Owner: postgres
--

CREATE INDEX receipt_transfer_history_receipt_id_idx ON collection.receipt_transfer_history USING btree (receipt_transfer_id);


--
-- TOC entry 3279 (class 1259 OID 61321)
-- Name: receipt_transfer_transferred_to_user_id_idx; Type: INDEX; Schema: collection; Owner: postgres
--

CREATE INDEX receipt_transfer_transferred_to_user_id_idx ON collection.receipt_transfer USING btree (transferred_to_user_id);


--
-- TOC entry 3284 (class 1259 OID 61322)
-- Name: registered_device_info_user_id_idx; Type: INDEX; Schema: collection; Owner: postgres
--

CREATE INDEX registered_device_info_user_id_idx ON collection.registered_device_info USING btree (user_id, deleted, status);


-- Completed on 2023-02-06 17:48:58

--
-- PostgreSQL database dump complete
--

