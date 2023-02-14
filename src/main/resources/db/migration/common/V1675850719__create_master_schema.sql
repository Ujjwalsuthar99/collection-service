--
-- PostgreSQL database dump
--

-- Dumped from database version 15.1
-- Dumped by pg_dump version 15.1

-- Started on 2023-02-08 15:33:53

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
-- TOC entry 8 (class 2615 OID 61211)
-- Name: master; Type: SCHEMA; Schema: -; Owner: postgres
--

--CREATE SCHEMA master;


ALTER SCHEMA master OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 236 (class 1259 OID 61270)
-- Name: support_contact_details; Type: TABLE; Schema: master; Owner: postgres
--

CREATE TABLE master.support_contact_details (
    support_contact_details_id bigint NOT NULL,
    created_date timestamp with time zone NOT NULL,
    created_by bigint NOT NULL,
    deleted boolean DEFAULT false,
    system_name character varying(50) NOT NULL,
    person_name character varying(100) NOT NULL,
    personal_email_id character varying(50),
    official_email_id character varying(50) NOT NULL,
    mobile_no bigint NOT NULL,
    whatsapp_mobile_no bigint,
    alt_mobile_no bigint,
    alt_mobile_no_2 bigint
);


ALTER TABLE master.support_contact_details OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 61274)
-- Name: support_contact_details_support_contact_details_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE master.support_contact_details_support_contact_details_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.support_contact_details_support_contact_details_id_seq OWNER TO postgres;

--
-- TOC entry 3375 (class 0 OID 0)
-- Dependencies: 237
-- Name: support_contact_details_support_contact_details_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE master.support_contact_details_support_contact_details_id_seq OWNED BY master.support_contact_details.support_contact_details_id;


--
-- TOC entry 3221 (class 2604 OID 61283)
-- Name: support_contact_details support_contact_details_id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY master.support_contact_details ALTER COLUMN support_contact_details_id SET DEFAULT nextval('master.support_contact_details_support_contact_details_id_seq'::regclass);


--
-- TOC entry 3368 (class 0 OID 61270)
-- Dependencies: 236
-- Data for Name: support_contact_details; Type: TABLE DATA; Schema: master; Owner: postgres
--



--
-- TOC entry 3376 (class 0 OID 0)
-- Dependencies: 237
-- Name: support_contact_details_support_contact_details_id_seq; Type: SEQUENCE SET; Schema: master; Owner: postgres
--

SELECT pg_catalog.setval('master.support_contact_details_support_contact_details_id_seq', 1, false);


--
-- TOC entry 3224 (class 2606 OID 61309)
-- Name: support_contact_details contact_us_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY master.support_contact_details
    ADD CONSTRAINT contact_us_pkey PRIMARY KEY (support_contact_details_id);


--
-- TOC entry 3225 (class 1259 OID 61323)
-- Name: support_contact_details_system_name_idx; Type: INDEX; Schema: master; Owner: postgres
--

CREATE INDEX support_contact_details_system_name_idx ON master.support_contact_details USING btree (system_name, deleted);


-- Completed on 2023-02-08 15:33:54

--
-- PostgreSQL database dump complete
--

