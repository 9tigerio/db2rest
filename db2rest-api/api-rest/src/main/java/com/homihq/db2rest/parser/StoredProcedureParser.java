package com.homihq.db2rest.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoredProcedureParser {
    private String procedureName;
    private List<String> parameters;

    public StoredProcedureParser() {
        this.parameters = new ArrayList<>();
    }

    public void parse(String procedureCall) throws IllegalArgumentException {
        // Validate input
        if (procedureCall == null || procedureCall.trim().isEmpty()) {
            throw new IllegalArgumentException("Procedure call cannot be null or empty");
        }

        // Pattern to match procedure name and parameters
        // Matches: procedure_name(param1, param2, ...)
        Pattern pattern = Pattern.compile("([\\w_]+)\\s*\\((.*)\\)");
        Matcher matcher = pattern.matcher(procedureCall.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid stored procedure call format");
        }

        // Extract procedure name
        procedureName = matcher.group(1);

        // Extract parameters
        String paramString = matcher.group(2);
        parseParameters(paramString);
    }

    private void parseParameters(String paramString) {
        parameters.clear();
        if (paramString.trim().isEmpty()) {
            return;
        }

        boolean inQuotes = false;
        StringBuilder currentParam = new StringBuilder();
        int nestedLevel = 0;

        for (char c : paramString.toCharArray()) {
            if (c == '\'' && !inQuotes) {
                inQuotes = true;
                currentParam.append(c);
            } else if (c == '\'' && inQuotes) {
                inQuotes = false;
                currentParam.append(c);
            } else if (c == '(' && !inQuotes) {
                nestedLevel++;
                currentParam.append(c);
            } else if (c == ')' && !inQuotes) {
                nestedLevel--;
                currentParam.append(c);
            } else if (c == ',' && !inQuotes && nestedLevel == 0) {
                parameters.add(currentParam.toString().trim());
                currentParam = new StringBuilder();
            } else {
                currentParam.append(c);
            }
        }

        // Add the last parameter
        if (currentParam.length() > 0) {
            parameters.add(currentParam.toString().trim());
        }
    }

    public String getProcedureName() {
        return procedureName;
    }

    public List<String> getParameters() {
        return new ArrayList<>(parameters);
    }

    // Main method with usage example
    public static void main(String[] args) {
        StoredProcedureParser parser = new StoredProcedureParser();
        
        try {
            // Test cases
            String[] testCalls = {
                "search_series_group('Absolute', col_1, col_2)",
                "complex_procedure('Test', nested_func(param1, param2), col_3)",
                "no_param_procedure()",
                "single_param_procedure('value')"
            };

            for (String call : testCalls) {
                System.out.println("\nParsing: " + call);
                parser.parse(call);
                System.out.println("Procedure Name: " + parser.getProcedureName());
                System.out.println("Parameters: " + parser.getParameters());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
