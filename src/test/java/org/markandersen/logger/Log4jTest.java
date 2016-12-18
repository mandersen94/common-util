package org.markandersen.logger;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 *
 */
public class Log4jTest {

    static{
        System.setProperty("log4j.debug", "true");
    }
    private Logger logger1 = Logger.getLogger("com.mark");
    private Logger logger2 = Logger.getLogger("com.mark.logger2");
    private Logger logger3 = Logger.getLogger("com.mark.logger3");



    @Test
    public void testLog4J(){


        logger1.debug("logger1, debug message.");

        logger1.info("logger1, info message.");

        logger2.debug("logger2, debug message.");

        logger2.info("logger2, info message.");


        logger3.debug("logger3, debug message.");

        logger3.info("logger3, info message.");

    }
}
