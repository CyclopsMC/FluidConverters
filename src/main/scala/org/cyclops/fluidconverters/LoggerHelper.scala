package org.cyclops.fluidconverters

import org.apache.logging.log4j.{Level, LogManager}

/**
 * Simple logger.
 * @author rubensworks
 */
object LoggerHelper {

    private def logger = LogManager.getLogger(Reference.MOD_NAME)

    def log(logLevel : Level, message : String) {
        logger.log(logLevel, message)
    }

    def log(message : String) {
        log(Level.INFO, message)
    }

}
