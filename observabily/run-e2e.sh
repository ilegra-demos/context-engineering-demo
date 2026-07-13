#!/bin/bash

# Exit on error
set -e

# Move to the script directory
cd "$(dirname "$0")"

FRONTEND_DIR="$(pwd)/frontend"

echo "========================================================================"
echo "Running E2E Test: ProductCatalogE2eTest"
echo "========================================================================"

docker run --rm --network="host" \
  -v "$FRONTEND_DIR":/app \
  -w /app \
  mcr.microsoft.com/playwright/java:v1.44.0-jammy \
  ./gradlew test --tests "com.example.demo.ProductCatalogE2eTest"
