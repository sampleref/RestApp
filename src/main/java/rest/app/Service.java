package rest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Created by Chakradhar
 */
public class Service {

    private static final Logger logger = LogManager.getLogger();
    private static ObjectMapper mapper = new ObjectMapper();
    private static final DecimalFormat df = new DecimalFormat("#");

    static {
        df.setMaximumFractionDigits(0);
    }

    public static void startServices() {
        startGetServices();
        startPostServices();
    }

    private static void startPostServices() {
        spark.Service postHttp = spark.Service.ignite()
                .port(8080);
        logger.debug("startPostServices listening on port 8080");
        postHttp.post("/transactions", (req, res) -> {
            final ObjectNode node = mapper.readValue(req.body(), ObjectNode.class);
            logger.debug(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node));
            double amount = node.get("amount").asDouble();
            long timestamp = node.get("timestamp").asLong();
            ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
            long minus60MilliSecs = utc.toInstant().toEpochMilli() - (60000);
            if (timestamp > minus60MilliSecs) {
                DataSource.saveTransaction(amount, timestamp);
                res.status(201);
            } else {
                res.status(204);
            }
            return "success";
        });
    }

    private static void startGetServices() {
        spark.Service getHttp = spark.Service.ignite()
                .port(8081);
        logger.debug("startGetServices listening on port 8081");
        getHttp.get("/statistics", (req, res) -> {
            res.status(200);
            res.type("application/json");
            double[] statistics = DataSource.getStatistics();
            return  "{\"sum\": \"" + df.format(statistics[0]) + "\",\"avg\": \""
                    + statistics[1] + "\",\"max\": \"" + statistics[2] + "\",\"min\": \""
                    + statistics[3] + "\",\"count\": \"" + statistics[4] + "\"}";
        });
    }
}
