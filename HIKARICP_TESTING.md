# HikariCP Load Testing Guide

This guide helps you understand and visualize HikariCP connection pool behavior in Grafana.

## Current Configuration

Your HikariCP configuration in `application.yml`:
```yaml
hikari:
  pool-name: AppMonitorHikariPool
  minimum-idle: 5              # Minimum connections maintained
  maximum-pool-size: 20        # Maximum connections allowed
  connection-timeout: 30000    # 30 seconds to get a connection
  idle-timeout: 600000         # 10 minutes idle before removal
  max-lifetime: 1800000        # 30 minutes max connection lifetime
  leak-detection-threshold: 60000  # 60 seconds to detect leaks
```

## How to Test

### Option 1: Use the Interactive Script
```bash
./hikaricp-load-test.sh
```

### Option 2: Manual curl Commands

#### 1. Light Load - Sequential Queries (Baseline)
```bash
curl http://localhost:8080/api/load-test/light | jq
```
**Expected Grafana Behavior:**
- Connection count: ~5-8 active connections
- Acquisition time: Very low
- Usage: Connections used one at a time

---

#### 2. Medium Load - 10 Concurrent Queries
```bash
curl http://localhost:8080/api/load-test/medium | jq
```
**Expected Grafana Behavior:**
- Connection count: ~10-12 active connections
- Pool grows to handle concurrent requests
- All within comfortable pool limits

---

#### 3. Heavy Load - 30 Concurrent Queries
```bash
curl "http://localhost:8080/api/load-test/heavy?concurrency=30" | jq
```
**Expected Grafana Behavior:**
- Connection count: Approaches maximum (20)
- Some queries may wait for available connections
- Acquisition time increases slightly
- Shows pool working at near-capacity

---

#### 4. Sustained Load - Long-Running Queries
```bash
# Hold 15 connections for 5 seconds
curl "http://localhost:8080/api/load-test/sustained?connections=15&holdTime=5000" | jq
```
**Expected Grafana Behavior:**
- Sustained plateau of 15 active connections
- Connections held for the specified duration
- Good for observing `leak-detection-threshold`

---

#### 5. Stress Test - Exceed Pool Size
```bash
curl http://localhost:8080/api/load-test/stress | jq
```
**Expected Grafana Behavior:**
- Connection count: Hits maximum (20)
- Some queries wait in queue (pending threads)
- Acquisition time increases significantly
- May see timeout warnings if load is extreme

---

#### 6. Check Stats
```bash
curl http://localhost:8080/api/load-test/stats | jq
```

---

## What to Observe in Grafana

### HikariCP Dashboard Metrics

#### 1. **Connection Status Graph**
- **Green line (Active)**: Connections currently executing queries
- **Yellow line (Idle)**: Connections waiting in the pool
- **Blue line (Pending)**: Threads waiting for a connection

**What you'll see:**
- Light load: ~5-8 connections
- Medium load: ~10-12 connections
- Heavy load: ~15-20 connections (near max)
- Stress test: Exactly 20 connections + pending threads

#### 2. **Connection Acquisition Usage**
- Shows how long it takes to acquire a connection from the pool
- **Low values (< 0.001)**: Healthy pool with available connections
- **High values (> 0.01)**: Pool under stress, queries waiting

#### 3. **Other Important Metrics**
- **Pool Size**: Total connections (active + idle)
- **Pending Threads**: Queries waiting for connections
- **Connection Timeout**: Queries that timed out waiting

---

## Testing Scenarios

### Scenario 1: Baseline Performance
```bash
# Run light load and observe normal behavior
curl http://localhost:8080/api/load-test/light
```
This shows your baseline connection usage.

### Scenario 2: Simulate Normal Traffic
```bash
# Run medium load repeatedly
for i in {1..10}; do 
  curl -s http://localhost:8080/api/load-test/medium | jq '.duration_ms'
  sleep 1
done
```
Watch how the pool maintains ~10-12 connections efficiently.

### Scenario 3: Traffic Spike
```bash
# Simulate sudden spike in traffic
curl "http://localhost:8080/api/load-test/heavy?concurrency=30"
```
See the pool grow to handle the spike, then shrink back down.

### Scenario 4: Continuous Load
```bash
# Generate continuous traffic for 1 minute
for i in {1..30}; do 
  curl -s http://localhost:8080/api/load-test/medium &
  sleep 2
done
wait
```
Observe sustained connection pool behavior over time.

### Scenario 5: Pool Exhaustion
```bash
# Push pool beyond capacity
curl http://localhost:8080/api/load-test/stress
```
See what happens when demand exceeds `maximum-pool-size: 20`.

---

## Understanding the Results

### Healthy Pool Behavior
- Connections scale up and down with demand
- Acquisition time stays very low (< 0.001)
- No pending threads
- Pool stays between `minimum-idle` and `maximum-pool-size`

### Pool Under Stress
- Connections hit `maximum-pool-size: 20`
- Acquisition time increases
- Pending threads appear
- Some queries may wait or timeout

### Configuration Tuning Tips

#### If you see many pending threads:
```yaml
maximum-pool-size: 30  # Increase from 20
```

#### If you see too many idle connections:
```yaml
minimum-idle: 3        # Decrease from 5
idle-timeout: 300000   # Decrease from 10 minutes
```

#### If you see connection timeouts:
```yaml
connection-timeout: 60000  # Increase from 30 seconds
maximum-pool-size: 25      # Increase pool size
```

---

## Pro Tips

1. **Watch Grafana in real-time** while running tests
2. **Run tests multiple times** to see consistent patterns
3. **Use the interactive script** for easy testing
4. **Start with light load** to establish baseline
5. **Gradually increase load** to find breaking point

---

## Troubleshooting

### No data in Grafana?
- Check if application is running: `curl http://localhost:8080/actuator/health`
- Verify Prometheus is scraping: `curl http://localhost:8080/actuator/prometheus | grep hikari`
- Check Grafana datasource configuration

### Connection pool not visible?
- Ensure HikariCP metrics are enabled (automatic with Actuator)
- Verify `management.endpoints.web.exposure.include: "*"` in application.yml
- Check if `spring-boot-starter-actuator` is in pom.xml

### Application crashes under load?
- Check application logs for connection timeout errors
- Verify database is running: `docker-compose ps`
- Consider increasing `maximum-pool-size` or `connection-timeout`

---

## Clean Up

After testing, restart the application to clear the connection pool:
```bash
# Stop the application
# Restart it to reset the pool
```

Or just let it idle - the pool will naturally shrink to `minimum-idle: 5` after the `idle-timeout`.
