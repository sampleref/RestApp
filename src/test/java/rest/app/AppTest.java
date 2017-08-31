package rest.app;

import com.despegar.http.client.HttpClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

/**
 * Unit test for Rest App.
 */
@Test
public class AppTest {

    private static final Logger logger = LogManager.getLogger();
    private static Random random = new Random();

    @BeforeTest
    public void init() throws Throwable {
        Service.startServices();
    }

    @Test
    public void testStatistics() throws HttpClientException, IOException, InterruptedException {
        postData();
        readData();
        Thread.sleep(10000);
    }

    private static void readData() {
        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    logger.info("Output /statistics");
                    get("/statistics").then().log().body();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error(e);
                    }
                }
            }
        });
        readThread.start();
    }

    private static void postData() {
        Thread postThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    given().body(getRandomAmountWithTimestamp()).accept(ContentType.JSON).
                            expect().
                            statusCode(201).
                            when().
                            post("/transactions");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error(e);
                    }
                }
            }
        });
        postThread.start();
    }

    private static String getRandomAmountWithTimestamp() {
        double amount = 1000 + (2000 - 1000) * random.nextDouble();
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        long timestamp = utc.toEpochSecond() * 1000;
        return "{\"amount\": \"" + amount + "\",\"timestamp\": \"" + timestamp + "\"}";
    }

}
