package rest.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Spark;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static spark.Spark.post;
import static spark.Spark.get;

/**
 * Created by kantipud on 31-08-2017.
 */
public class Service {

    private static final Logger logger = LogManager.getLogger();
    private static ObjectMapper mapper = new ObjectMapper();

    public static void startServices() {
        int httpPort = 8080;
        Spark.port(httpPort);
        logger.debug("HTTP listening on port " + httpPort);

        post("/transactions", (req, res) -> {
            final ObjectNode node = mapper.readValue(req.body(), ObjectNode.class);
            logger.debug(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node));
            double amount = node.get("amount").asDouble();
            long timestamp = node.get("timestamp").asLong();
            DataSource.saveTransaction(amount, timestamp);
            ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
            long minus60MilliSecs = (utc.toEpochSecond() - 60) * 1000;
            if (timestamp < minus60MilliSecs) {
                res.status(204);
            } else {
                res.status(201);
            }
            return "success";
        });

        get("/statistics", (req, res) -> {
            res.status(200);
            res.type("application/json");
            int[] statistics = DataSource.getStatistics();
            String response = "{\"sum\": \"" + statistics[0] + "\",\"avg\": \""
                    + statistics[1] + "\",\"max\": \"" + statistics[2] + "\",\"min\": \""
                    + statistics[3] + "\",\"count\": \"" + statistics[4] + "\"}";
            return response;
        });
    }
}
