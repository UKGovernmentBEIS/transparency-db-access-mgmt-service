CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE public.subsidy_measure_version
(
    version uuid PRIMARY KEY NOT NULL DEFAULT uuid_generate_v1(),
    sc_number VARCHAR(255) REFERENCES SUBSIDY_MEASURE(SC_NUMBER) NOT NULL,
    ga_id numeric REFERENCES GRANTING_AUTHORITY(GA_ID) NOT NULL,
    subsidy_measure_title VARCHAR(255) NOT NULL,
    start_date date NOT NULL,
    end_date date,
    duration numeric(36, 1) NOT NULL,
    budget VARCHAR(255) NOT NULL,
    ga_subsidy_weblink VARCHAR(500),
    ga_subsidy_weblink_description VARCHAR(255),
    published_measure_date date NOT NULL,
    created_by VARCHAR(255),
    approved_by VARCHAR(255),
    status VARCHAR(255),
    created_timestamp timestamp without time zone,
    last_modified_timestamp timestamp without time zone,
    deleted_by VARCHAR(255),
    deleted_timestamp timestamp without time zone,
    has_no_end_date boolean NOT NULL,
    spending_sectors VARCHAR(5000),
    confirmation_date date,
    subsidy_scheme_description text,
    maximum_amount_under_scheme VARCHAR(255),
    legal_basis_text VARCHAR(5000)
);

ALTER TABLE IF EXISTS public.subsidy_measure_version
    OWNER to postgres;