-- Create EMP schema for testing
CREATE SCHEMA EMP;

-- Create sample employee table
CREATE TABLE EMP.EMPLOYEE (
    ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    FIRST_NAME VARCHAR(50) NOT NULL,
    LAST_NAME VARCHAR(50) NOT NULL,
    EMAIL VARCHAR(100) UNIQUE,
    PHONE VARCHAR(20),
    HIRE_DATE DATE,
    SALARY DECIMAL(10,2),
    DEPARTMENT_ID INTEGER,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
);

-- Create sample department table
CREATE TABLE EMP.DEPARTMENT (
    ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    NAME VARCHAR(100) NOT NULL,
    DESCRIPTION VARCHAR(500),
    MANAGER_ID INTEGER,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (ID)
);

-- Insert sample departments
INSERT INTO EMP.DEPARTMENT (NAME, DESCRIPTION) VALUES 
('Engineering', 'Software development and engineering'),
('Human Resources', 'Employee relations and recruitment'),
('Sales', 'Sales and customer relations'),
('Marketing', 'Marketing and brand management');

-- Insert sample employees
INSERT INTO EMP.EMPLOYEE (FIRST_NAME, LAST_NAME, EMAIL, PHONE, HIRE_DATE, SALARY, DEPARTMENT_ID) VALUES 
('John', 'Doe', 'john.doe@company.com', '555-0101', '2023-01-15', 75000.00, 1),
('Jane', 'Smith', 'jane.smith@company.com', '555-0102', '2023-02-20', 80000.00, 1),
('Bob', 'Johnson', 'bob.johnson@company.com', '555-0103', '2023-03-10', 65000.00, 2),
('Alice', 'Williams', 'alice.williams@company.com', '555-0104', '2023-04-05', 70000.00, 3),
('Charlie', 'Brown', 'charlie.brown@company.com', '555-0105', '2023-05-12', 72000.00, 4);

-- Add foreign key constraint
ALTER TABLE EMP.EMPLOYEE ADD CONSTRAINT FK_EMP_DEPT 
    FOREIGN KEY (DEPARTMENT_ID) REFERENCES EMP.DEPARTMENT(ID);

-- Create indexes for better performance
CREATE INDEX IDX_EMP_DEPT ON EMP.EMPLOYEE(DEPARTMENT_ID);
CREATE INDEX IDX_EMP_EMAIL ON EMP.EMPLOYEE(EMAIL);

-- Grant permissions
GRANT ALL ON SCHEMA EMP TO USER db2inst1;
GRANT ALL ON EMP.EMPLOYEE TO USER db2inst1;
GRANT ALL ON EMP.DEPARTMENT TO USER db2inst1;
