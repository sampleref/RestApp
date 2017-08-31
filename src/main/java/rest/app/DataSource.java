package rest.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by kantipud on 31-08-2017.
 */
public class DataSource {

    private static final Logger logger = LogManager.getLogger();

    public static void saveTransaction(double amount, long timestamp) {
        logger.debug("Received {} and {} ", amount, timestamp);
    }

    /**
     * order-> sum, avg, max, min, count
     */
    public static int[] getStatistics() {
        int[] statistics = {100, 50, 70, 30, 20};
        return statistics;
    }
}
