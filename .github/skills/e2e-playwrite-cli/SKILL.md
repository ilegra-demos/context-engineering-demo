---
name: 'e2e-playwrite-cli'
description: 'Manually loaded agentic skill to plan, explore, implement, and validate Java Playwright E2E tests using the isolated playwright-cli Docker container and Node scripts instead of host dependencies.'
trigger: 'manual'
inputs:
  - route: 'The frontend URL path to inspect and test (e.g., "/")'
---

# Skill: e2e-playwrite-cli

You are an expert Software Engineering and QA Agent tasked with planning, exploring, implementing, and validating E2E (End-to-End) tests using **Playwright Java** for the `@observabily/frontend` module.

This skill is executed **manually** upon user request and receives a specific **route/feature** to test as input. Unlike the `e2e-playwrite-mcp` version, this skill uses direct CLI commands and native Playwright screenshot tools inside an isolated container to inspect the application.

---

## Skill Execution Flow

### Step 1: Environment Setup & Dependency Validation
Before proceeding with any test development, verify that the observability stack and the isolated Playwright container are up and running:
1. Ensure the docker-compose services (including `playwright-cli`) are active:
   ```bash
   cd observabily
   docker compose up -d playwright-cli
   ```
2. Verify that the `playwright` package is installed globally inside the container:
   ```bash
   docker compose exec playwright-cli npm install -g playwright@1.44.0
   ```
3. Validate that the Playwright CLI responds correctly from inside the container:
   ```bash
   docker compose exec playwright-cli npx --yes playwright@1.44.0 --version
   ```
4. Verify that the frontend application is running and accessible on the host:
   - Frontend: `http://localhost:8081`

---

### Step 2: Interactive Page Exploration
Instead of using MCP browser tools, you must explore the target page (`http://frontend:8081{{route}}`) using standard shell and Playwright command line tools:
1. Fetch the raw HTML of the page directly from the host to understand the initial server-side rendered DOM structure (forms, inputs, and attributes like `data-testid`):
   ```bash
   curl -s http://localhost:8081{{route}}
   ```
2. Take a visual screenshot of the target page using the Playwright CLI screenshot tool inside the container (which uses the internal container network hostname `frontend`):
   ```bash
   docker compose exec playwright-cli npx --yes playwright@1.44.0 screenshot http://frontend:8081{{route}} scratch/explore_screenshot.png
   ```
3. Inspect the returned HTML and the screenshot file saved in the `scratch/` folder to map the exact CSS selectors (such as `data-testid`, IDs, or classes) of buttons, input fields, and output elements.

---

### Step 3: Test Case Planning
Using the DOM structure identified during the exploration step:
1. **Happy Path:** Define the primary successful flow (e.g., navigating to the page, filling inputs, submitting the form, and asserting the target element becomes visible).
2. **Edge Cases & Error Handling:** Define error states (e.g., empty form submissions, connection failures, validation errors) and how they manifest in the UI.
3. List the exact CSS selectors (e.g., `[data-testid='input-id']`) and corresponding assertions you will write.

---

### Step 4: Java E2E Test Implementation
Implement or update the E2E test class in the `observabily/frontend/src/test/java/com/example/demo/` directory (`ProductCatalogE2eTest.java`):
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
   ./run-e2e.sh
   ```
2. If the build or tests fail, inspect the error output, re-run the exploration commands (such as taking screenshots with the Playwright CLI) to check the UI state, and adjust selectors or assertions until the test passes.

---

## Usage Example via Agent Chat

To manually trigger this skill through agent chat, prompt the AI assistant by referencing this skill file and supplying the required input parameters:

```markdown
Please run the skill @.github/skills/e2e-playwrite-cli/SKILL.md with inputs:
- route: "/"
```

Or:

```markdown
Execute the manual `e2e-playwrite-cli` skill for route "/"
```
