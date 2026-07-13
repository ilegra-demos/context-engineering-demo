#!/bin/bash

# Exit on error
set -e

# Move to the script directory
cd "$(dirname "$0")"

FRONTEND_DIR="$(pwd)/frontend"

show_usage() {
    echo "Usage: $0 [mcp|cli|all]"
    echo "  mcp  - Run E2E test matching the Playwright MCP server skill (ProductCatalogE2eTest)"
    echo "  cli  - Run E2E test matching the Playwright CLI skill (ProductCatalogCliE2eTest)"
    echo "  all  - Run both E2E tests"
    exit 1
}

if [ "$#" -ne 1 ]; then
    show_usage
fi

APPROACH=$1

run_test() {
    local test_name=$1
    echo "========================================================================"
    echo "Running E2E Test: $test_name"
    echo "========================================================================"
    
    docker run --rm --network="host" \
      -v "$FRONTEND_DIR":/app \
      -w /app \
      mcr.microsoft.com/playwright/java:v1.44.0-jammy \
      ./gradlew test --tests "com.example.demo.$test_name"
}

case "$APPROACH" in
    mcp)
        run_test "ProductCatalogE2eTest"
        ;;
    cli)
        run_test "ProductCatalogCliE2eTest"
        ;;
    all)
        run_test "ProductCatalogE2eTest"
        run_test "ProductCatalogCliE2eTest"
        ;;
    *)
        show_usage
        ;;
esac
