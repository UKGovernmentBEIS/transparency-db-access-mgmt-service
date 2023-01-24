CREATE SEQUENCE IF NOT EXISTS admin_program_read_seq START 10001;

CREATE TABLE IF NOT EXISTS ADMIN_PROGRAM
(
    AP_NUMBER       		VARCHAR(255)	PRIMARY KEY DEFAULT 'AP' || NEXTVAL('admin_program_read_seq') NOT NULL,
    SC_NUMBER               VARCHAR(255)    REFERENCES SUBSIDY_MEASURE(SC_NUMBER) NOT NULL,
    GA_ID				    NUMERIC			REFERENCES GRANTING_AUTHORITY(GA_ID) NOT NULL,
    ADMIN_PROGRAM_NAME      VARCHAR(255)    NOT NULL,
    BUDGET          		NUMERIC(36,2)	NOT NULL,
    STATUS				    VARCHAR(255)	DEFAULT 'DRAFT',
    CREATED_BY			    VARCHAR(255)	DEFAULT 'SYSTEM',
    CREATED_TIMESTAMP		TIMESTAMP 		WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    LAST_MODIFIED_TIMESTAMP	TIMESTAMP		WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    DELETED_BY			    VARCHAR(255),
    DELETED_TIMESTAMP       TIMESTAMP 		WITHOUT TIME ZONE
);