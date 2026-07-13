# Copilot Token Observability Demo

This folder contains a local observability stack that ingests OpenTelemetry telemetry from GitHub Copilot Chat and visualizes token usage in Grafana.

## What this demo shows

The provisioned Grafana dashboard includes a dedicated section named **Copilot Token Observability** with:
- Total, input, and output token counters
- Token throughput over time
- Token usage by model
- Top users/sessions/workspaces by token usage
- Token usage by outcome status

The section includes metadata filters for:
- `model`
- `provider`
- `request_type`
- `user`
- `session_id`
- `workspace`
- `language`
- `ide`
- `status`
- `git_branch`

## Prerequisites

1. Docker + Docker Compose installed.
2. VS Code with GitHub Copilot Chat enabled.
3. Workspace settings with Copilot OTEL export enabled:
   - `.vscode/settings.json`
   - `github.copilot.chat.otel.enabled: true`
   - `github.copilot.chat.otel.otlpEndpoint: http://localhost:4318`
4. Playwright and MCP dependencies are fully containerized (no local Node/npm installs required):
   - The MCP approach runs using the `playwright-mcp` service container.
   - The CLI approach runs using the isolated `playwright-cli` service container.
   - E2E tests are executed inside the official Playwright Java docker runner.

## Start the stack

From this folder:

```bash
docker compose up -d --build
```

Services:
- OTEL Collector: `localhost:4318` (OTLP HTTP), `localhost:4317` (OTLP gRPC)
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3005`

## Running E2E Tests

You can run the end-to-end tests using the helper script:

```bash
./run-e2e.sh
```

## Use the dashboard

1. Open Grafana at `http://localhost:3005`.
2. Go to folder `Observability`.
3. Open dashboard `Java Spring Boot + Copilot OTel Observability Dashboard`.
4. Run different Copilot interactions in VS Code to generate telemetry.
5. Use the dashboard filters to slice token usage by metadata.

## Notes

- V1 focuses on token observability only (no currency cost calculations).
- Some filters can remain empty if a label is not emitted by the current Copilot telemetry event.
- The dashboard is intentionally provisioned from repository files so teammates can run this demo with the same setup.

## Troubleshooting

If Copilot token panels are empty:

1. Confirm Copilot OTEL settings are loaded from `.vscode/settings.json`.
2. Generate some Copilot activity (chat prompts and code suggestions).
3. Check metric names currently available in Prometheus:

```bash
curl -s 'http://localhost:9090/api/v1/label/__name__/values'
```

The dashboard uses regex selectors for token-like metric families, so it should adapt to naming changes as long as the exported metric names contain terms such as `token`, `tokens`, `copilot`, or `chat`.
