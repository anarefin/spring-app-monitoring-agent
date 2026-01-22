#!/bin/bash

echo "╔════════════════════════════════════════════════════════════╗"
echo "║         HikariCP Connection Pool Visualization Test       ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""
echo "📊 STEP 1: Open Grafana Dashboard"
echo "   URL: http://localhost:3000"
echo "   Navigate to: Dashboards > Spring Boot Resource Tuning"
echo "   Find the panel: 'HikariCP (Connection Pool)'"
echo ""
echo "📊 STEP 2: Watch the dashboard and keep it visible"
echo ""
echo "🚀 STEP 3: Running test NOW..."
echo ""
echo "   Test: 15 concurrent connections"
echo "   Duration: 10 seconds"
echo "   Expected: Green line (Active) should jump to ~15"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Start the test
echo "⏳ Starting test in 3 seconds... WATCH GRAFANA NOW!"
sleep 1
echo "⏳ 2..."
sleep 1
echo "⏳ 1..."
sleep 1
echo ""
echo "🔥 TEST RUNNING - WATCH YOUR GRAFANA DASHBOARD!"
echo ""

# Execute the test
curl -s "http://localhost:8080/api/load-test/visual?connections=15&holdSeconds=10" | jq -C '.'

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "✅ Test completed!"
echo ""
echo "Did you see the green line jump to ~15 in Grafana?"
echo ""
echo "If NOT, check:"
echo "  1. Grafana time range is set to 'Last 15 minutes'"
echo "  2. Click the refresh button in Grafana"
echo "  3. Verify Prometheus is scraping:"
echo "     curl http://localhost:8080/actuator/prometheus | grep hikaricp_connections_active"
echo ""
echo "To run different tests:"
echo "  - More connections: curl \"http://localhost:8080/api/load-test/visual?connections=20&holdSeconds=15\""
echo "  - Heavy load: curl \"http://localhost:8080/api/load-test/heavy\""
echo "  - Interactive menu: ./hikaricp-load-test.sh"
echo ""
