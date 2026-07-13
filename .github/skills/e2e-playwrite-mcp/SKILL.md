---
name: 'e2e-playwrite-mcp'
description: 'Manually loaded agentic skill to plan, explore, implement, and validate Java Playwright E2E tests using the docker-compose Playwright MCP server.'
trigger: 'manual'
inputs:
  - route: 'The frontend URL path to inspect and test (e.g., "/")'
tools:
  - playwright
---

# Skill: e2e-playwrite-mcp

You are an expert Software Engineering and QA Agent tasked with planning, exploring, implementing, and validating E2E (End-to-End) tests using **Playwright Java** for the `@observabily/frontend` module.

This skill is executed **manually** upon user request and receives a specific **route/feature** to test as input. Unlike the `e2e-playwrite-cli` version, this skill utilizes the browser tools of the `playwright` MCP server to interactively explore the application.

---

## Skill Execution Flow

### Step 1: Environment Setup & Dependency Validation
Before proceeding with any test development, verify that the observability stack and the Playwright MCP server are up and running:
1. Ensure the docker-compose services (including `playwright-mcp`) are active:
   ```bash
   cd observabily
   docker compose up -d --build
   ```
2. Verify that the endpoints are responsive:
   - Frontend: `http://localhost:8081`
   - Playwright MCP Server: `http://localhost:8931/sse`

---

### Step 2: Interactive Page Exploration
You **must** use the tools provided by the `playwright` MCP server (connected via `http://localhost:8931/sse`) to explore the page:
1. Navigate to the frontend page using the internal container network hostname (since the MCP server container runs inside the `observability-net` network):
   - **Target URL to open via MCP tool:** `http://frontend:8081{{route}}` (e.g., `http://frontend:8081/`)
2. Inspect the page DOM, structure, and accessibility trees using the MCP browser tools (e.g., screenshots, accessibility tree, console outputs) to identify the forms, inputs, and button selectors (such as `data-testid` attributes).
3. If necessary, simulate clicks, text input, and other interactions using the MCP server tools to test user flows before writing the test code.

---

### Step 3: Test Case Planning
Using the DOM structure identified during the exploration step:
1. **Happy Path:** Define the primary successful flow (e.g., navigating to the page, filling inputs, submitting the form, and asserting the target element becomes visible).
2. **Edge Cases & Error Handling:** Define error states (e.g., empty form submissions, connection failures, validation errors) and how they manifest in the UI.
3. List the exact CSS selectors (e.g., `[data-testid='input-id']`) and corresponding assertions you will write.

---

### Step 4: Java E2E Test Implementation
Implement or update the E2E test class in the `observabily/frontend/src/test/java/com/example/demo/` directory (e.g., `ProductCatalogE2eTest.java`):
- Use JUnit 5 annotations (`org.junit.jupiter.api.*`).
- Instantiate and use Playwright Java client APIs (`com.microsoft.playwright.*`).
- **URL Configuration:** Since the Java tests execute using the host network, they must navigate to the host port:
  ```java
  page.navigate("http://localhost:8081{{route}}");
  ```

---

### Step 5: Test Execution & Validation
Execute and debug the tests to ensure everything is correct and green:
1. Run the test suite using the Gradle wrapper within the Playwright Java runner container by executing the helper script from the `observabily/` directory:
   ```bash
   ./run-e2e.sh mcp
   ```
2. If the build or tests fail, inspect the error output, re-run the exploration tools via the Playwright MCP server to check the UI state, and adjust selectors or assertions until the test passes.

---

## Usage Example via Agent Chat

To manually trigger this skill through agent chat, prompt the AI assistant by referencing this skill file and supplying the required input parameters:

```markdown
Please run the skill @.github/skills/e2e-playwrite-mcp/SKILL.md with inputs:
- route: "/"
```

Or:

```markdown
Execute the manual `e2e-playwrite-mcp` skill for route "/"
```
