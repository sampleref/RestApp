package rest.app;

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
    public void testStatistics() throws IOException, InterruptedException {
        for(int index = 1000; index > 0; index--){
            postData();
        }
        readData();
        postDataOlder();
        Thread.sleep(300000);
    }

    private static void readData() {
        Thread readThread = new Thread(() -> {
            while (true) {
                get("http://localhost:8081/statistics").then().log().body();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error(e);
                }
            }
        });
        readThread.start();
    }

    private static void postData() {
        Thread postThread = new Thread(() -> {
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
        });
        postThread.start();
    }

    private static void postDataOlder() {
        Thread postThread = new Thread(() -> {
            while (true) {
                given().body(getRandomAmountWithOlderTimestamp()).accept(ContentType.JSON).
                        expect().
                        statusCode(204).
                        when().
                        post("/transactions");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    logger.error(e);
                }
            }
        });
        postThread.start();
    }

    private static String getRandomAmountWithTimestamp() {
        double amount = 1000 + (2000 - 1000) * random.nextDouble();
        amount = (double) Math.round(amount * 100) / 100;
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        long timestamp = utc.toInstant().toEpochMilli();
        return "{\"amount\": \"" + amount + "\",\"timestamp\": \"" + timestamp + "\"}";
    }

    private static String getRandomAmountWithOlderTimestamp() {
        double amount = 1000 + (2000 - 1000) * random.nextDouble();
        amount = (double) Math.round(amount * 100) / 100;
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        long timestamp = utc.toInstant().toEpochMilli() - 60010;
        return "{\"amount\": \"" + amount + "\",\"timestamp\": \"" + timestamp + "\"}";
    }

}
