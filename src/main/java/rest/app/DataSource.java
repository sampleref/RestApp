package rest.app;

import com.stoyanr.evictor.ConcurrentMapWithTimedEviction;
import com.stoyanr.evictor.map.ConcurrentHashMapWithTimedEviction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.stream.Collectors;

/**
 * Created by Chakradhar
 */
public class DataSource {

    private static final Logger logger = LogManager.getLogger();
    private static final ConcurrentMapWithTimedEviction<Double, Long> MAP_WITH_TIMED_EVICTION = new ConcurrentHashMapWithTimedEviction<>();

    public static void saveTransaction(double amount, long timestamp) {
        logger.debug("Received {} and {} ", amount, timestamp);
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        long timeNowMillis = utc.toInstant().toEpochMilli();
        long timeMillisLeft = (60 * 1000) - (timeNowMillis - timestamp);
        MAP_WITH_TIMED_EVICTION.put(amount, timestamp, timeMillisLeft);
    }

    /**
     * order-> sum, avg, max, min, count
     */
    public static double[] getStatistics() {
        long startTime = timeNowInMillis();
        DoubleSummaryStatistics amtSummary = MAP_WITH_TIMED_EVICTION.keySet().stream().collect(Collectors.summarizingDouble(Double::doubleValue));
        long endTime = timeNowInMillis();
        logger.info("Time taken in milli secs {} for {} items", (endTime - startTime), amtSummary.getCount());
        return new double[]{amtSummary.getSum(), amtSummary.getAverage(), amtSummary.getMax(), amtSummary.getMin(), amtSummary.getCount()};
    }

    private static long timeNowInMillis() {
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        return utc.toInstant().toEpochMilli();
    }
}
