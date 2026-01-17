package gatling;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;

public class LoadTestSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    ScenarioBuilder scn = scenario("Basic Load Test")
            .exec(
                    http("Get All Products")
                            .get("/api/products"))
            .pause(1)
            .exec(
                    http("Get Product By ID")
                            .get("/api/products/#{randomInt(1,50)}"));

    {
        setUp(
                scn.injectOpen(
                        nothingFor(4),
                        atOnceUsers(10),
                        rampUsers(50).during(Duration.ofSeconds(10))))
                .protocols(httpProtocol);
    }
}
