CREATE TABLE employees (
                           id SERIAL PRIMARY KEY,       -- Unique employee ID
                           name TEXT NOT NULL,          -- Employee name
                           department_id INT,           -- Department ID (foreign key to departments table, if exists)
                           salary NUMERIC(10, 2)        -- Employee salary
);

INSERT INTO employees (name, department_id, salary) VALUES
                                                        ('John Doe', 1, 50000.00),
                                                        ('Jane Smith', 1, 55000.00),
                                                        ('Alice Johnson', 2, 60000.00),
                                                        ('Bob Brown', 2, 65000.00);

CREATE OR REPLACE FUNCTION add_numbers(a INT, b INT)
        RETURNS INT
        LANGUAGE plpgsql
AS $$
    BEGIN
    RETURN a + b;
    END;
$$;


CREATE OR REPLACE FUNCTION get_employee_details(dept_id INT)
    RETURNS TABLE (employee_id INT, employee_name TEXT, salary NUMERIC)
       AS $$
    BEGIN
    RETURN QUERY
        SELECT id, name, salary
        FROM employees
        WHERE department_id = dept_id;
    END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE update_employee_salary(emp_id INT, new_salary NUMERIC)
    AS $$
    BEGIN
        UPDATE employees
        SET salary = new_salary
        WHERE id = emp_id;
    END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION increment_value(INOUT value INT)
    AS $$
        BEGIN
        value := value + 1;
        END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION multiply_numbers(a INT, b INT DEFAULT 10)
    RETURNS INT AS $$
        BEGIN
        RETURN a * b;
        END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION get_employee_info(emp_id INT, OUT emp_name TEXT, OUT emp_salary NUMERIC)
    AS $$
    BEGIN
        SELECT name, salary INTO emp_name, emp_salary
        FROM employees
        WHERE id = emp_id;
    END;
$$ LANGUAGE plpgsql;

-- 7. Procedure with Multiple Input Parameters and Transaction Handling

CREATE OR REPLACE PROCEDURE transfer_funds(from_account INT, to_account INT, amount NUMERIC)
    AS $$
    BEGIN
        -- Start a transaction
    BEGIN
            -- Deduct amount from the source account
    UPDATE accounts SET balance = balance - amount WHERE id = from_account;

    -- Add amount to the target account
    UPDATE accounts SET balance = balance + amount WHERE id = to_account;

    -- Commit the transaction
    COMMIT;
    EXCEPTION
            WHEN OTHERS THEN
                -- Rollback the transaction in case of an error
                ROLLBACK;
                RAISE EXCEPTION 'Error transferring funds: %', SQLERRM;
    END;
    END;
$$ LANGUAGE plpgsql;

-- CALL transfer_funds(1, 2, 100); -- Transfers 100 units from account 1 to account 2

-- 8. Function with VARIADIC Input Parameters


   CREATE OR REPLACE FUNCTION sum_variadic(VARIADIC numbers INT[])
RETURNS INT AS $$
DECLARE
total INT := 0;
    num INT;
BEGIN
    FOREACH num IN ARRAY numbers LOOP
        total := total + num;
END LOOP;
RETURN total;
END;
$$ LANGUAGE plpgsql;


-- 9. Function with JSON Input and Output
CREATE OR REPLACE FUNCTION modify_json(input_data JSON)
RETURNS JSON AS $$
BEGIN
RETURN json_build_object(
        'name', input_data->>'name',
        'age', (input_data->>'age')::INT + 1
       );
END;
$$ LANGUAGE plpgsql;

-- SELECT modify_json('{"name": "John", "age": 30}'); -- Returns {"name": "John", "age": 31}


-- 10. Function with Cursor Output

CREATE OR REPLACE FUNCTION get_employees_cursor()
RETURNS REFCURSOR AS $$
DECLARE
emp_cursor REFCURSOR := 'employee_cursor';
BEGIN
OPEN emp_cursor FOR
SELECT id, name, salary
FROM employees;
RETURN emp_cursor;
END;
$$ LANGUAGE plpgsql;

   -- BEGIN;
   -- SELECT get_employees_cursor();
   -- FETCH ALL FROM employee_cursor;
   -- COMMIT;
