-- Create employee table
CREATE TABLE employee (
                          id INT IDENTITY(1,1) PRIMARY KEY,
                          name NVARCHAR(100) NOT NULL
);

-- Insert sample employee
INSERT INTO employee (name) VALUES ('John Doe');

-- Query to verify data
SELECT * FROM employee;
