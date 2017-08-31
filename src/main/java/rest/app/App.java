package rest.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 */
public class App 
{
    private static final Logger logger = LogManager.getLogger();

    public static void main( String[] args )
    {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                kill();
            }
        });
        Service.startServices();
    }

    public static void kill() {
       logger.debug("Shutting down app");
    }
}
