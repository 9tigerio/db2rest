-- Create employee table
CREATE TABLE employee (
                          id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          name VARCHAR2(100) NOT NULL
);

-- Insert sample employee
INSERT INTO employee (name) VALUES ('John Doe');

-- Query to verify data
SELECT * FROM employee;
