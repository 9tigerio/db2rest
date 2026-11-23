-- Table with a nullable tenant column, used by PgMultiTenancyTest to check which
-- rows users with and without a tenant role can see.
CREATE TABLE tenant_notes (
    id integer PRIMARY KEY,
    note varchar(100) NOT NULL,
    tenant_id integer NULL
);

INSERT INTO tenant_notes (id, note, tenant_id) VALUES
    (1, 'shared', NULL),
    (2, 'tenant 15', 15),
    (3, 'tenant 7', 7);
