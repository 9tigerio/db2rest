-- Create employee table
CREATE TABLE employee (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL
);

-- Insert sample employee
INSERT INTO employee (name) VALUES ('John Doe');

-- Query to verify data
SELECT * FROM employee;
