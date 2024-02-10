Let's improve writing tests for better readability and maintainability
1. Please define headers, query params together and make sure those are organised
2. Most of the test data are separated into ITestUtils.java, to reuse test data
3. Avoid redundant assertions, tests should be concise. For e.g. no need to have both these assertions:
.andExpect(jsonPath("$.rows").isArray())
.andExpect(jsonPath("$.rows", hasSize(2)))
If the rows has size > 1, it is implicit that it is an array
4. On the contrary, please provide with just enough assertions. 
For e.g. don't just assert the error like .andExpect(status().isBadRequest()), assert the error details as well.
Please remember: Tests are another source of documentation to determine what the code does. 
We must make the tests readable.