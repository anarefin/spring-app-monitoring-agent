#!/bin/bash

echo "╔════════════════════════════════════════════════════════════╗"
echo "║         HikariCP Debug Test with Metrics Monitoring       ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Function to get current metrics
get_metrics() {
    active=$(curl -s http://localhost:8080/actuator/prometheus | grep "hikaricp_connections_active{" | grep -v "^#" | awk '{print $2}')
    idle=$(curl -s http://localhost:8080/actuator/prometheus | grep "hikaricp_connections_idle{" | grep -v "^#" | awk '{print $2}')
    total=$(curl -s http://localhost:8080/actuator/prometheus | grep "hikaricp_connections{" | grep -v "^#" | grep -v "active" | grep -v "idle" | grep -v "pending" | awk '{print $2}')
    pending=$(curl -s http://localhost:8080/actuator/prometheus | grep "hikaricp_connections_pending{" | grep -v "^#" | awk '{print $2}')
    
    echo "   Active: ${active:-0} | Idle: ${idle:-0} | Total: ${total:-0} | Pending: ${pending:-0}"
}

echo "📊 Current Metrics (BEFORE test):"
get_metrics
echo ""

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "🚀 Starting load test with 15 connections for 10 seconds"
echo "   Open Grafana now: http://localhost:3000"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Start the test in background
curl -s "http://localhost:8080/api/load-test/visual?connections=15&holdSeconds=10" > /tmp/hikari-test-result.json &
TEST_PID=$!

echo "⏳ Monitoring metrics while test is running..."
echo ""

# Monitor metrics during the test
for i in {1..12}; do
    sleep 1
    echo "📊 Second $i:"
    get_metrics
done

# Wait for test to complete
wait $TEST_PID

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "✅ Test Result:"
cat /tmp/hikari-test-result.json | jq -C '.'
echo ""

echo "📊 Current Metrics (AFTER test):"
get_metrics
echo ""

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "🔍 Analysis:"
echo ""
echo "If you saw Active connections jump to ~15 during seconds 1-10,"
echo "then the test is working correctly!"
echo ""
echo "The metrics above should match what you see in Grafana."
echo ""
echo "If Active stayed at 0 the entire time, there might be a"
echo "transaction management issue. Check the application logs:"
echo "   docker logs app-monitor"
echo ""
