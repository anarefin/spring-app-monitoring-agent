#!/bin/bash

# HikariCP Load Test Script
# This script helps you trigger different load scenarios to observe HikariCP behavior in Grafana

BASE_URL="http://localhost:8080/api/load-test"

echo "=========================================="
echo "HikariCP Load Test Menu"
echo "=========================================="
echo ""
echo "Watch Grafana dashboard while running these tests:"
echo "http://localhost:3000/d/spring-boot-resource-tuning"
echo ""
echo "Available tests:"
echo ""
echo "1. Light Load     - Sequential queries (baseline)"
echo "2. Medium Load    - 10 concurrent queries (2s hold)"
echo "3. Heavy Load     - 30 concurrent queries (3s hold)"
echo "4. Visual Test    - 15 connections held for 10 seconds (BEST FOR GRAFANA)"
echo "5. Sustained Load - Hold 15 connections for 5 seconds"
echo "6. Stress Test    - 50 concurrent queries (exceeds pool size)"
echo "7. Continuous     - Run medium load repeatedly"
echo "8. Stats          - Check endpoint status"
echo ""
echo "q. Quit"
echo ""

while true; do
    read -p "Select test (1-8, q to quit): " choice
    
    case $choice in
        1)
            echo ""
            echo "Running Light Load Test..."
            curl -s "$BASE_URL/light" | jq '.'
            echo ""
            ;;
        2)
            echo ""
            echo "Running Medium Load Test..."
            curl -s "$BASE_URL/medium" | jq '.'
            echo ""
            ;;
        3)
            echo ""
            echo "Running Heavy Load Test..."
            curl -s "$BASE_URL/heavy?concurrency=30" | jq '.'
            echo ""
            ;;
        4)
            echo ""
            echo "🎯 VISUAL TEST - Perfect for Grafana!"
            echo "=========================================="
            echo "Watch your Grafana dashboard NOW!"
            echo "15 connections will be held for 10 seconds"
            echo ""
            curl -s "$BASE_URL/visual?connections=15&holdSeconds=10" | jq '.'
            echo ""
            ;;
        5)
            echo ""
            echo "Running Sustained Load Test..."
            echo "(This will hold connections for 5 seconds)"
            curl -s "$BASE_URL/sustained?connections=15&holdTime=5000" | jq '.'
            echo ""
            ;;
        6)
            echo ""
            echo "Running Stress Test..."
            echo "(This may cause connection timeouts)"
            curl -s "$BASE_URL/stress" | jq '.'
            echo ""
            ;;
        7)
            echo ""
            echo "Running Continuous Medium Load..."
            echo "Press Ctrl+C to stop"
            echo ""
            while true; do
                curl -s "$BASE_URL/medium" | jq '.duration_ms, .queries'
                sleep 2
            done
            ;;
        8)
            echo ""
            curl -s "$BASE_URL/stats" | jq '.'
            echo ""
            ;;
        q|Q)
            echo "Exiting..."
            exit 0
            ;;
        *)
            echo "Invalid choice. Please select 1-8 or q."
            ;;
    esac
done
